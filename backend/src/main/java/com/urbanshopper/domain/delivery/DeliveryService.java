package com.urbanshopper.domain.delivery;

import com.urbanshopper.domain.order.Order;
import com.urbanshopper.domain.order.OrderRepository;
import com.urbanshopper.domain.order.OrderStatus;
import com.urbanshopper.domain.order.OrderStateMachine;
import com.urbanshopper.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Delivery Service — manages the physical delivery flow from market to customer.
 *
 * Business rules from 03-business-rules.md:
 * - E-002: Proof of delivery (GPS, photo, authorized recipient, customer confirmation)
 * - E-003: Dynamic ETA prediction
 * - E-004: Unavailable customer procedure (5-min contact attempt, safe drop, return)
 * - E-005: Wrong address handling
 * - E-007: Category-based inspection windows
 * - E-008: Redelivery after failed attempt
 * - E-009: ETA recalculation and delay reporting
 */
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryService.class);

    private final DeliveryRepository deliveryRepository;
    private final DeliveryPhotoRepository deliveryPhotoRepository;
    private final OrderRepository orderRepository;
    private final OrderStateMachine orderStateMachine;

    // ──────────────────────────────────────────────
    //  Start Delivery (E-003)
    // ──────────────────────────────────────────────

    /**
     * E-003: Shopper begins delivery to customer.
     * Transitions order RECEIPT_VERIFIED → IN_DELIVERY.
     * Calculates initial ETA based on estimated travel time.
     *
     * For MVP, ETA uses shopper-provided estimate. Future: Maps API road distance + traffic.
     */
    @Transactional
    public DeliveryDTO startDelivery(UUID orderId, DeliveryStartRequest req, UUID shopperId) {
        var order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.RECEIPT_VERIFIED) {
            throw new BusinessException("INVALID_STATE",
                "Order must be in RECEIPT_VERIFIED state to start delivery. Current: " + order.getStatus());
        }

        // Transition order state machine
        orderStateMachine.transition(order, OrderStatus.IN_DELIVERY,
            "DeliveryStarted", "shopper", shopperId, null);

        // Calculate ETA
        var now = Instant.now();
        var eta = now.plusSeconds(req.estimatedTravelMinutes() * 60L);

        // Create delivery record
        var delivery = Delivery.builder()
            .orderId(orderId)
            .shopperId(shopperId)
            .customerId(order.getCustomerId())
            .status("in_transit")
            .startedAt(now)
            .etaAt(eta)
            .originalEtaAt(eta)
            .notes(req.notes())
            .build();

        var saved = deliveryRepository.save(delivery);
        log.info("Delivery started for order {}: ETA {} ({} min)",
            order.getOrderNumber(), eta, req.estimatedTravelMinutes());

        return DeliveryDTO.fromEntity(saved);
    }

    // ──────────────────────────────────────────────
    //  Confirm Delivery (E-002)
    // ──────────────────────────────────────────────

    /**
     * E-002: Shopper confirms delivery at customer location.
     * Validates GPS position, stores photo evidence, records recipient.
     * Transitions order IN_DELIVERY → DELIVERED.
     * Sets inspection window deadline per E-007.
     */
    @Transactional
    public DeliveryDTO confirmDelivery(UUID orderId, DeliveryConfirmRequest req, UUID shopperId) {
        var order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.IN_DELIVERY) {
            throw new BusinessException("INVALID_STATE",
                "Order must be in IN_DELIVERY state. Current: " + order.getStatus());
        }

        var delivery = deliveryRepository.findByOrderId(orderId)
            .orElseThrow(() -> new BusinessException("DELIVERY_NOT_FOUND",
                "Delivery not started for order: " + orderId));

        var now = Instant.now();

        // ── GPS verification (stub — real verification uses Maps API) ──
        if (req.latitude() != null && req.longitude() != null) {
            delivery.setProofLat(req.latitude());
            delivery.setProofLng(req.longitude());
        }

        // ── Recipient information (E-002) ──
        if (req.isAuthorizedRecipient() != null && req.isAuthorizedRecipient()) {
            delivery.setIsAuthorizedRecipient(true);
            delivery.setRecipientName(req.recipientName());
            delivery.setRecipientRelationship(req.recipientRelationship());
        } else {
            delivery.setRecipientName(req.recipientName());
        }

        delivery.setCustomerConfirmed(req.customerConfirmed() != null && req.customerConfirmed());
        delivery.setArrivalAt(now);
        delivery.setNotes(req.notes());
        delivery.setStatus("delivered");

        // ── Save delivery photos (E-002) ──
        if (req.photoUrls() != null) {
            for (int i = 0; i < req.photoUrls().size(); i++) {
                var photo = DeliveryPhoto.builder()
                    .deliveryId(delivery.getId())
                    .photoUrl(req.photoUrls().get(i))
                    .photoType("proof")
                    .latitude(req.latitude())
                    .longitude(req.longitude())
                    .build();
                deliveryPhotoRepository.save(photo);
            }
        }

        // ── Calculate inspection window (E-007) ──
        // MVP default: 30 minutes inspection window for mixed/general orders
        var inspectionDeadline = now.plusSeconds(1800); // 30 min default
        delivery.setInspectionDeadline(inspectionDeadline);

        deliveryRepository.save(delivery);

        // ── Transition order state ──
        orderStateMachine.transition(order, OrderStatus.DELIVERED,
            "ShopperArrivedAndDelivered", "shopper", shopperId, null);

        log.info("Delivery confirmed for order {} (recipient: {}, photos: {})",
            order.getOrderNumber(), req.recipientName(),
            req.photoUrls() != null ? req.photoUrls().size() : 0);

        return DeliveryDTO.fromEntity(delivery);
    }

    // ──────────────────────────────────────────────
    //  Report Delay (E-009)
    // ──────────────────────────────────────────────

    /**
     * E-009: Shopper reports delay during delivery.
     * Recalculates ETA, records reason for analytics.
     * Customer notification handled by Notification Service.
     */
    @Transactional
    public DeliveryDTO reportDelay(UUID orderId, DeliveryDelayRequest req, UUID shopperId) {
        var delivery = deliveryRepository.findByOrderId(orderId)
            .orElseThrow(() -> new BusinessException("DELIVERY_NOT_FOUND",
                "No active delivery for order: " + orderId));

        if (!"in_transit".equals(delivery.getStatus())) {
            throw new BusinessException("INVALID_STATE",
                "Delay can only be reported while in transit");
        }

        // Recalculate ETA
        var now = Instant.now();
        if (delivery.getEtaAt() != null) {
            var newEta = delivery.getEtaAt().plusSeconds(req.additionalMinutes() * 60L);
            delivery.setEtaAt(newEta);
        }
        delivery.setDelayMinutes(delivery.getDelayMinutes() + req.additionalMinutes());
        delivery.setDelayReason(req.reason());
        delivery.setDelayReportedAt(now);

        var saved = deliveryRepository.save(delivery);

        log.info("Delay reported for order {}: +{} min (reason: {})",
            orderId, req.additionalMinutes(), req.reason());

        return DeliveryDTO.fromEntity(saved);
    }

    // ──────────────────────────────────────────────
    //  Report Unavailable Customer (E-004)
    // ──────────────────────────────────────────────

    /**
     * E-004: Shopper arrives but customer is not available.
     * After 5-minute contact attempt, shopper may:
     * - Leave at safe drop location (if pre-authorised)
     * - Return items to market
     */
    @Transactional
    public DeliveryDTO reportCustomerUnavailable(UUID orderId, String safeDropLocation, UUID shopperId) {
        var delivery = deliveryRepository.findByOrderId(orderId)
            .orElseThrow(() -> new BusinessException("DELIVERY_NOT_FOUND",
                "No active delivery for order: " + orderId));

        if (!"in_transit".equals(delivery.getStatus())) {
            throw new BusinessException("INVALID_STATE",
                "Cannot report unavailable customer in " + delivery.getStatus() + " state");
        }

        var now = Instant.now();
        delivery.setUnavailableAttemptedAt(now);

        if (safeDropLocation != null && !safeDropLocation.isBlank()) {
            // Safe drop — mark as delivered with photo evidence
            delivery.setSafeDropLocation(safeDropLocation);
            delivery.setStatus("delivered");
            delivery.setArrivalAt(now);
            delivery.setIsAuthorizedRecipient(true);
            delivery.setInspectionDeadline(now.plusSeconds(1800));

            var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found"));
            orderStateMachine.transition(order, OrderStatus.DELIVERED,
                "UnavailableCustomerSafeDrop", "shopper", shopperId, "Left at safe drop location");
        } else {
            // Return items
            delivery.setItemsReturned(true);
            delivery.setStatus("returned");
            delivery.setNotes("Customer unavailable — items returned to market");

            var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found"));
            orderStateMachine.transition(order, OrderStatus.CANCELLED,
                "UnavailableCustomerReturned", "system", null,
                "Customer unavailable, items returned to market");
        }

        var saved = deliveryRepository.save(delivery);
        return DeliveryDTO.fromEntity(saved);
    }

    // ──────────────────────────────────────────────
    //  Mark Completed (E-007)
    // ──────────────────────────────────────────────

    /**
     * E-007: Mark delivery as completed after inspection window elapses
     * or customer confirms receipt.
     * Transitions order DELIVERED → COMPLETED.
     */
    @Transactional
    public DeliveryDTO completeDelivery(UUID orderId, UUID actorId) {
        var order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessException("INVALID_STATE",
                "Order must be DELIVERED to complete. Current: " + order.getStatus());
        }

        var delivery = deliveryRepository.findByOrderId(orderId)
            .orElseThrow(() -> new BusinessException("DELIVERY_NOT_FOUND",
                "No delivery record for order: " + orderId));

        var now = Instant.now();
        delivery.setStatus("completed");
        delivery.setCompletedAt(now);

        orderStateMachine.transition(order, OrderStatus.COMPLETED,
            "InspectionWindowElapsed", "system", actorId, null);

        var saved = deliveryRepository.save(delivery);
        log.info("Delivery completed for order {}", orderId);

        return DeliveryDTO.fromEntity(saved);
    }

    // ──────────────────────────────────────────────
    //  Get Delivery ETA (E-003)
    // ──────────────────────────────────────────────

    @Transactional(readOnly = true)
    public DeliveryEtaResponse getDeliveryEta(UUID orderId) {
        var order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found: " + orderId));

        var delivery = deliveryRepository.findByOrderId(orderId).orElse(null);

        return DeliveryEtaResponse.builder()
            .orderId(orderId.toString())
            .orderStatus(order.getStatus().name())
            .deliveryStatus(delivery != null ? delivery.getStatus() : "not_started")
            .etaAt(delivery != null && delivery.getEtaAt() != null ? delivery.getEtaAt().toString() : null)
            .originalEtaAt(delivery != null && delivery.getOriginalEtaAt() != null
                ? delivery.getOriginalEtaAt().toString() : null)
            .arrivalAt(delivery != null && delivery.getArrivalAt() != null
                ? delivery.getArrivalAt().toString() : null)
            .delayMinutes(delivery != null ? delivery.getDelayMinutes() : 0)
            .delayReason(delivery != null ? delivery.getDelayReason() : null)
            .build();
    }
}
