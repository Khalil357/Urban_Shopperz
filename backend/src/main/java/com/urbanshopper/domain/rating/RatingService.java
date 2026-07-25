package com.urbanshopper.domain.rating;

import com.urbanshopper.domain.order.OrderRepository;
import com.urbanshopper.domain.order.OrderStatus;
import com.urbanshopper.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Rating Service — two-way feedback system (H-001 through H-006).
 *
 * H-001: Customer rates shopper (1-5, criteria breakdown, 72hr window)
 * H-002: Shopper rates customer privately (internal Trust Score only)
 * H-003: Recency-weighted average (or simple average for <10 ratings)
 * H-006: Blind period — reveal when both submit or 72hrs pass
 */
@Service
@RequiredArgsConstructor
public class RatingService {

    private static final Logger log = LoggerFactory.getLogger(RatingService.class);
    private static final long SUBMISSION_WINDOW_HOURS = 72;
    private static final long BLIND_PERIOD_HOURS = 72;
    private static final int RECENCY_THRESHOLD = 10;
    private static final double RECENCY_WEIGHT = 0.7;
    private static final double HISTORIC_WEIGHT = 0.3;

    private final RatingRepository ratingRepository;
    private final OrderRepository orderRepository;

    /**
     * H-001/H-002: Submit a rating for an order.
     *
     * @param orderId    the completed order
     * @param raterId    the user submitting the rating
     * @param raterType  "customer" or "shopper"
     * @param req        rating details
     * @return the created rating
     */
    @Transactional
    public RatingDTO submitRating(UUID orderId, UUID raterId, String raterType, SubmitRatingRequest req) {
        var order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found"));

        if (order.getStatus() != OrderStatus.COMPLETED && order.getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessException("INVALID_STATE",
                "Can only rate completed/delivered orders. Current: " + order.getStatus());
        }

        // Check 72-hour submission window
        var completedAt = order.getUpdatedAt();
        if (completedAt != null && Duration.between(completedAt, Instant.now()).toHours() > SUBMISSION_WINDOW_HOURS) {
            throw new BusinessException("RATING_WINDOW_CLOSED",
                "Ratings must be submitted within 72 hours of order completion");
        }

        // Check duplicate rating
        if (ratingRepository.findByOrderIdAndRaterType(orderId, raterType).isPresent()) {
            throw new BusinessException("DUPLICATE_RATING",
                "You have already submitted a rating for this order");
        }

        // Determine ratee
        UUID rateeId;
        if ("customer".equals(raterType)) {
            rateeId = order.getShopperId();
            if (rateeId == null) {
                throw new BusinessException("NO_SHOPPER", "No shopper was assigned to this order");
            }
        } else {
            rateeId = order.getCustomerId();
        }

        // Create rating
        var rating = Rating.builder()
            .orderId(orderId)
            .raterId(raterId)
            .raterType(raterType)
            .rateeId(rateeId)
            .score(req.score())
            .itemAccuracy(req.itemAccuracy())
            .itemQuality(req.itemQuality())
            .timeliness(req.timeliness())
            .communication(req.communication())
            .professionalism(req.professionalism())
            .feedback(req.feedback())
            .build();

        var saved = ratingRepository.save(rating);

        // H-006: Blind period — check if both parties have now rated
        checkAndReveal(orderId);

        log.info("Rating submitted: order={}, rater={} ({}), score={}/5",
            orderId, raterId, raterType, req.score());

        return RatingDTO.fromEntity(saved);
    }

    /**
     * H-003: Get recency-weighted rating summary for a shopper.
     *
     * - < 10 ratings: simple average
     * - >= 10 ratings: 70% recent (last 30 days), 30% historic
     */
    @Transactional(readOnly = true)
    public ShopperRatingSummary getShopperRatingSummary(UUID shopperId) {
        var allRatings = ratingRepository.findByRateeIdAndRaterTypeOrderByCreatedAtDesc(
            shopperId, "customer");
        var validRatings = allRatings.stream()
            .filter(r -> !r.getIsFlagged())
            .toList();

        long total = validRatings.size();
        if (total == 0) {
            return ShopperRatingSummary.builder()
                .shopperId(shopperId.toString())
                .averageScore(0.0)
                .totalRatings(0L)
                .recentRatings(0L)
                .recentAverage(0.0)
                .build();
        }

        double average;
        long recentCount = 0;
        double recentAverage = 0;

        if (total < RECENCY_THRESHOLD) {
            // Simple average for low sample size
            average = validRatings.stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0);
        } else {
            // Recency-weighted: last 30 days are "recent"
            var cutoff = Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS);
            var recent = validRatings.stream()
                .filter(r -> r.getCreatedAt().isAfter(cutoff))
                .toList();
            var historic = validRatings.stream()
                .filter(r -> !r.getCreatedAt().isAfter(cutoff))
                .toList();

            recentCount = recent.size();
            if (recentCount > 0) {
                recentAverage = recent.stream().mapToInt(Rating::getScore).average().orElse(0);
            }

            double historicAverage = historic.stream().mapToInt(Rating::getScore).average().orElse(0);

            if (recentCount > 0) {
                average = RECENCY_WEIGHT * recentAverage + HISTORIC_WEIGHT * historicAverage;
            } else {
                average = historicAverage;
            }
        }

        return ShopperRatingSummary.builder()
            .shopperId(shopperId.toString())
            .averageScore(Math.round(average * 100.0) / 100.0)
            .totalRatings(total)
            .recentRatings(recentCount)
            .recentAverage(Math.round(recentAverage * 100.0) / 100.0)
            .build();
    }

    @Transactional(readOnly = true)
    public List<RatingDTO> getShopperRatings(UUID shopperId) {
        return ratingRepository.findByRateeIdAndRaterTypeOrderByCreatedAtDesc(shopperId, "customer")
            .stream()
            .map(RatingDTO::fromEntity)
            .toList();
    }

    /**
     * H-006: Blind period check.
     * If both customer and shopper have rated, reveal both ratings.
     */
    private void checkAndReveal(UUID orderId) {
        var customerRating = ratingRepository.findByOrderIdAndRaterType(orderId, "customer");
        var shopperRating = ratingRepository.findByOrderIdAndRaterType(orderId, "shopper");

        if (customerRating.isPresent() && shopperRating.isPresent()) {
            customerRating.get().setIsRevealed(true);
            shopperRating.get().setIsRevealed(true);
            ratingRepository.save(customerRating.get());
            ratingRepository.save(shopperRating.get());
            log.info("Blind period lifted for order {}: both parties have rated", orderId);
        }
    }
}
