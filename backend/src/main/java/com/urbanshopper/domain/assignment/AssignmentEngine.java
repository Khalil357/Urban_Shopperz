package com.urbanshopper.domain.assignment;

import com.urbanshopper.domain.order.*;
import com.urbanshopper.shared.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Assignment Engine — matches queued orders to eligible shoppers.
 *
 * Implements business rules:
 * - C-002: Haversine + Road distance (Haversine for MVP)
 * - C-003: 30-second acceptance window
 * - C-004: 3-minute maximum offer cascade
 * - C-005: Per-zone maximum assignment radius
 * - C-008: Fairness through weighted Assignment Score
 * - C-011: Multi-factor Assignment Score
 * - B-010: Single active order per shopper
 */
@Component
@RequiredArgsConstructor
public class AssignmentEngine {

    private static final Logger log = LoggerFactory.getLogger(AssignmentEngine.class);

    private static final int OFFER_TIMEOUT_SECONDS = 30;
    private static final int CASCADE_TIMEOUT_SECONDS = 180; // 3 minutes
    private static final int MAX_CANDIDATES = 20;

    private final OrderRepository orderRepository;
    private final OrderStateMachine orderStateMachine;
    private final OrderOfferRepository offerRepository;
    private final ShopperAvailabilityRepository availabilityRepository;
    private final ZoneConfigRepository zoneConfigRepository;
    private final com.urbanshopper.domain.order.MarketRepository marketRepository;
    private final AssignmentScoreCalculator scoreCalculator;
    private final DistanceCalculator distanceCalculator;

    // ═══════════════════════════════════════════════
    //  Public API
    // ═══════════════════════════════════════════════

    /**
     * Process a specific order for assignment.
     * Called directly after order creation or by the scheduled poller.
     */
    @Transactional
    public void processOrder(UUID orderId) {
        var order = orderRepository.findById(orderId).orElse(null);
        if (order == null || order.getStatus() != OrderStatus.QUEUED_FOR_ASSIGNMENT) {
            return; // Already assigned or no longer queued
        }

        var zoneConfig = zoneConfigRepository.findById(order.getZoneId()).orElse(null);
        if (zoneConfig == null || !"active".equals(zoneConfig.getStatus())) {
            failAssignment(order, "Zone not active: " + order.getZoneId());
            return;
        }

        var maxRadius = zoneConfig.getMaxAssignmentRadiusKm().doubleValue();
        var availableShoppers = findEligibleShoppers(order, maxRadius);

        if (availableShoppers.isEmpty()) {
            failAssignment(order, "No eligible shoppers available in zone");
            return;
        }

        // Create first offer to highest-scoring shopper
        createOffer(order, availableShoppers.get(0), 1, maxRadius);
    }

    /**
     * Accept an offer — transitions order to ACCEPTED.
     */
    @Transactional
    public ShopperOfferDTO acceptOffer(UUID offerId, UUID shopperId) {
        var offer = offerRepository.findById(offerId)
            .orElseThrow(() -> new BusinessException("OFFER_NOT_FOUND", "Offer not found"));

        if (!offer.getShopperId().equals(shopperId)) {
            throw new BusinessException("UNAUTHORIZED", "This offer is not for you");
        }
        if (!"pending".equals(offer.getStatus())) {
            throw new BusinessException("OFFER_EXPIRED", "Offer is no longer valid");
        }
        if (offer.getExpiresAt().isBefore(Instant.now())) {
            offer.setStatus("timed_out");
            offerRepository.save(offer);
            cascadeToNext(offer);
            throw new BusinessException("OFFER_EXPIRED", "Offer has expired");
        }

        var order = orderRepository.findById(offer.getOrderId())
            .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found"));

        // Mark offer as accepted
        offer.setStatus("accepted");
        offer.setRespondedAt(Instant.now());
        offerRepository.save(offer);

        // Set shopper on order
        order.setShopperId(shopperId);

        // Transition state: OFFERED → ACCEPTED
        orderStateMachine.transition(order, OrderStatus.ACCEPTED,
            "ShopperAcceptedOffer", "shopper", shopperId, null);

        // Update shopper availability — set to busy/assigned
        availabilityRepository.findByShopperId(shopperId).ifPresent(a -> {
            a.setStatus("assigned");
            a.setHeartbeatAt(Instant.now());
            availabilityRepository.save(a);
        });

        log.info("Offer {} accepted by shopper {} for order {}",
            offerId, shopperId, order.getOrderNumber());

        return ShopperOfferDTO.fromEntity(offer, order);
    }

    /**
     * Decline an offer — cascades to next eligible shopper.
     */
    @Transactional
    public void declineOffer(UUID offerId, UUID shopperId) {
        var offer = offerRepository.findById(offerId)
            .orElseThrow(() -> new BusinessException("OFFER_NOT_FOUND", "Offer not found"));

        if (!offer.getShopperId().equals(shopperId)) {
            throw new BusinessException("UNAUTHORIZED", "This offer is not for you");
        }
        if (!"pending".equals(offer.getStatus())) {
            return; // Idempotent
        }

        offer.setStatus("declined");
        offer.setRespondedAt(Instant.now());
        offerRepository.save(offer);

        log.info("Offer {} declined by shopper {}", offerId, shopperId);

        // Cascade to next shopper
        cascadeToNext(offer);
    }

    // ═══════════════════════════════════════════════
    //  Scheduled Tasks (poll every 5 seconds)
    // ═══════════════════════════════════════════════

    /**
     * Poll for queued orders that need assignment.
     */
    @Scheduled(fixedDelay = 5000)
    public void checkAndAssign() {
        var queuedOrders = orderRepository.findByStatus(OrderStatus.QUEUED_FOR_ASSIGNMENT);
        for (var order : queuedOrders) {
            try {
                processOrder(order.getId());
            } catch (Exception e) {
                log.error("Error processing order {}: {}", order.getId(), e.getMessage());
            }
        }
    }

    /**
     * Poll for expired offers and handle cascade/timeout.
     */
    @Scheduled(fixedDelay = 5000)
    public void checkExpiredOffers() {
        var expired = offerRepository.findExpiredOffers(Instant.now());
        for (var offer : expired) {
            if (!"pending".equals(offer.getStatus())) continue;

            offer.setStatus("timed_out");
            offerRepository.save(offer);

            log.info("Offer {} timed out for shopper {}", offer.getId(), offer.getShopperId());

            try {
                cascadeToNext(offer);
            } catch (Exception e) {
                log.error("Error cascading from expired offer {}: {}", offer.getId(), e.getMessage());
            }
        }
    }

    // ═══════════════════════════════════════════════
    //  Private — Eligibility & Scoring
    // ═══════════════════════════════════════════════

    /**
     * Find eligible shoppers for an order, sorted by Assignment Score descending.
     */
    private List<EligibleShopper> findEligibleShoppers(Order order, double maxRadius) {
        // Find online shoppers in the same zone
        var onlineShoppers = availabilityRepository
            .findByStatusAndCurrentZoneId("online", order.getZoneId());

        // Get market coordinates for distance calculation
        var marketCoords = getMarketCoordinates(order);

        return onlineShoppers.stream()
            .map(s -> evaluateShopper(s, marketCoords, maxRadius))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .sorted(Comparator.comparing((EligibleShopper e) -> e.score().total()).reversed())
            .limit(MAX_CANDIDATES)
            .toList();
    }

    private Optional<EligibleShopper> evaluateShopper(
            ShopperAvailability shopper, MarketCoords market, double maxRadius) {

        var shopperLat = shopper.getCurrentLat().doubleValue();
        var shopperLon = shopper.getCurrentLng().doubleValue();

        // Haversine distance check
        var distanceKm = distanceCalculator.haversineKm(
            shopperLat, shopperLon, market.lat(), market.lon());

        // C-005: Filter by max assignment radius
        if (!distanceCalculator.isWithinRadius(distanceKm, maxRadius)) {
            return Optional.empty();
        }

        // Build score input with default values (no work history = new shopper defaults)
        var scoreInput = new AssignmentScoreCalculator.ScoreInput(
            shopperLat, shopperLon, market.lat(), market.lon(), maxRadius,
            0, 0, 0, 4.0, 0, 0, 30);

        var score = scoreCalculator.calculate(scoreInput);
        return Optional.of(new EligibleShopper(shopper.getShopperId(), score));
    }

    /**
     * Create an offer for a shopper and transition order to OFFERED.
     */
    private void createOffer(Order order, EligibleShopper candidate, int cascadeRound, double maxRadius) {
        var now = Instant.now();
        var expiresAt = now.plusSeconds(OFFER_TIMEOUT_SECONDS);

        var offer = OrderOffer.builder()
            .orderId(order.getId())
            .shopperId(candidate.shopperId())
            .score(BigDecimal.valueOf(candidate.score().total().doubleValue())
                .setScale(2, RoundingMode.HALF_UP))
            .scoreDistance(candidate.score().distance())
            .scoreAcceptance(candidate.score().acceptanceRate())
            .scoreCompletion(candidate.score().completionRate())
            .scoreRating(candidate.score().rating())
            .scoreWorkload(candidate.score().workload())
            .scoreActivity(candidate.score().activity())
            .scoreZonePriority(candidate.score().zonePriority())
            .cascadeRound(cascadeRound)
            .offeredAt(now)
            .expiresAt(expiresAt)
            .distanceKm(BigDecimal.valueOf(candidate.score().distanceKm())
                .setScale(2, RoundingMode.HALF_UP))
            .build();

        offerRepository.save(offer);

        // Transition order to OFFERED (only on first offer)
        if (cascadeRound == 1) {
            orderStateMachine.transition(order, OrderStatus.OFFERED,
                "OrderReadyForAssignment", "system", null, null);
        }

        log.info("Offer {} created for shopper {} (score: {}, round: {})",
            offer.getId(), candidate.shopperId(), candidate.score().total(), cascadeRound);
    }

    /**
     * Cascade to the next shopper after a decline or timeout.
     * If 3 minutes have elapsed since the first offer, fail the assignment.
     */
    private void cascadeToNext(OrderOffer declinedOffer) {
        var order = orderRepository.findById(declinedOffer.getOrderId())
            .orElse(null);
        if (order == null) return;

        // Check if order is still in OFFERED state
        if (order.getStatus() != OrderStatus.OFFERED) return;

        // Check 3-minute cascade timeout (C-004)
        var offers = offerRepository.findByOrderIdOrderByCascadeRoundAsc(order.getId());
        if (!offers.isEmpty()) {
            var elapsed = Duration.between(offers.get(0).getOfferedAt(), Instant.now()).getSeconds();
            if (elapsed >= CASCADE_TIMEOUT_SECONDS) {
                failAssignment(order, "Cascade timeout — no shopper accepted within 3 minutes");
                return;
            }
        }

        // Find the next candidate — shoppers who haven't been offered yet
        var offeredShopperIds = offerRepository.findByOrderIdOrderByCascadeRoundAsc(order.getId())
            .stream()
            .map(OrderOffer::getShopperId)
            .toList();

        var zoneConfig = zoneConfigRepository.findById(order.getZoneId()).orElse(null);
        if (zoneConfig == null) {
            failAssignment(order, "Zone not found");
            return;
        }

        var maxRadius = zoneConfig.getMaxAssignmentRadiusKm().doubleValue();
        var allEligible = findEligibleShoppers(order, maxRadius);

        var nextCandidate = allEligible.stream()
            .filter(s -> !offeredShopperIds.contains(s.shopperId()))
            .findFirst();

        if (nextCandidate.isPresent()) {
            var nextRound = declinedOffer.getCascadeRound() + 1;
            createOffer(order, nextCandidate.get(), nextRound, maxRadius);
        } else {
            failAssignment(order, "All eligible shoppers have been offered — none accepted");
        }
    }

    /**
     * Fail the assignment — cancel the order with cascade timeout reason.
     */
    private void failAssignment(Order order, String reason) {
        log.warn("Assignment failed for order {}: {}", order.getOrderNumber(), reason);
        orderStateMachine.transition(order, OrderStatus.CANCELLED,
            "CascadeTimeout", "system", null, reason);
        order.setCancellationReason(reason);
        order.setCancelledBy("system");
        order.setCancelledAt(Instant.now());
        orderRepository.save(order);
    }

    /**
     * Get market coordinates for distance calculation.
     * Uses the order's assigned market if available; falls back to zone center.
     */
    private MarketCoords getMarketCoordinates(Order order) {
        if (order.getMarketId() != null) {
            var market = marketRepository.findById(order.getMarketId()).orElse(null);
            if (market != null && market.getLatitude() != null && market.getLongitude() != null) {
                return new MarketCoords(market.getLatitude().doubleValue(), market.getLongitude().doubleValue());
            }
        }
        // Fallback: zone center
        var zoneConfig = zoneConfigRepository.findById(order.getZoneId()).orElse(null);
        if (zoneConfig != null && zoneConfig.getCenterLat() != null) {
            return new MarketCoords(
                zoneConfig.getCenterLat().doubleValue(),
                zoneConfig.getCenterLng().doubleValue());
        }
        // Default: Dar es Salaam center
        return new MarketCoords(-6.7924, 39.2083);
    }

    private record EligibleShopper(UUID shopperId, AssignmentScore score) {}
    private record MarketCoords(double lat, double lon) {}
}
