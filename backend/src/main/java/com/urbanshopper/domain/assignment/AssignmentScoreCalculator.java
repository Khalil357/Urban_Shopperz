package com.urbanshopper.domain.assignment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * C-011: Intelligent Assignment Score calculation.
 *
 * Assignment Score = Distance (35%) + Acceptance Rate (20%) + Completion Rate (15%)
 *                  + Customer Rating (10%) + Current Workload (10%)
 *                  + Recent Activity (5%) + Zone Priority (5%)
 *
 * New shoppers (first 30 days) use a modified formula where distance is weighted at 50%
 * and performance factors are derived from limited available data.
 */
@Component
@RequiredArgsConstructor
public class AssignmentScoreCalculator {

    private final DistanceCalculator distanceCalculator;

    // Weights (sum = 100%)
    private static final double WEIGHT_DISTANCE = 0.35;
    private static final double WEIGHT_ACCEPTANCE = 0.20;
    private static final double WEIGHT_COMPLETION = 0.15;
    private static final double WEIGHT_RATING = 0.10;
    private static final double WEIGHT_WORKLOAD = 0.10;
    private static final double WEIGHT_ACTIVITY = 0.05;
    private static final double WEIGHT_ZONE_PRIORITY = 0.05;

    // New shopper adjustment
    private static final double NEW_SHOPPER_DISTANCE_WEIGHT = 0.50;
    private static final int NEW_SHOPPER_DAYS = 30;

    /**
     * Calculate the full Assignment Score for a shopper relative to an order.
     */
    public AssignmentScore calculate(ScoreInput input) {
        // Distance score (straight-line Haversine)
        var distanceKm = distanceCalculator.haversineKm(
            input.shopperLat(), input.shopperLon(),
            input.marketLat(), input.marketLon());
        var distanceScore = distanceCalculator.distanceScore(distanceKm, input.maxRadiusKm());
        var distanceScoreBd = toBd(distanceScore);

        // Acceptance Rate (B-011): accepted / total offers
        var acceptRate = input.totalOffers() > 0
            ? (double) input.acceptedOffers() / input.totalOffers() * 100
            : 50; // default for new shoppers
        var acceptScoreBd = toBd(acceptRate);

        // Completion Rate (B-007): completed / accepted
        var completionRate = input.acceptedOffers() > 0
            ? (double) input.completedOrders() / input.acceptedOffers() * 100
            : 90; // optimistic default for new shoppers
        var completionScoreBd = toBd(completionRate);

        // Customer Rating (H-001): 1-5 stars → score 0-100
        var ratingScore = input.averageRating() * 20; // 4.0 * 20 = 80
        var ratingScoreBd = toBd(ratingScore);

        // Current Workload: fewer active orders = higher score
        // If shopper has 0 active orders: 100, 1 active: 50, 2+: 0
        var workloadScore = Math.max(0, 100 - (input.activeOrders() * 50));
        var workloadScoreBd = toBd(workloadScore);

        // Recent Activity: was shopper active recently?
        var activityScore = input.minutesSinceLastActivity() < 5 ? 100
            : input.minutesSinceLastActivity() < 15 ? 80
            : input.minutesSinceLastActivity() < 30 ? 60
            : 40;
        var activityScoreBd = toBd(activityScore);

        // Zone Priority: default 50 (can be boosted for preferred zones)
        var zoneScoreBd = toBd(50);

        // Weighted total
        boolean isNewShopper = input.daysSinceRegistration() < NEW_SHOPPER_DAYS;
        double total;
        if (isNewShopper) {
            // New shopper: distance matters more, less data on performance
            total = NEW_SHOPPER_DISTANCE_WEIGHT * distanceScore
                  + 0.15 * acceptRate
                  + 0.15 * completionRate
                  + 0.10 * ratingScore
                  + 0.05 * workloadScore
                  + 0.05 * activityScore;
        } else {
            total = WEIGHT_DISTANCE * distanceScore
                  + WEIGHT_ACCEPTANCE * acceptRate
                  + WEIGHT_COMPLETION * completionRate
                  + WEIGHT_RATING * ratingScore
                  + WEIGHT_WORKLOAD * workloadScore
                  + WEIGHT_ACTIVITY * activityScore
                  + WEIGHT_ZONE_PRIORITY * 50;
        }

        return new AssignmentScore(
            toBd(total), distanceScoreBd, acceptScoreBd, completionScoreBd,
            ratingScoreBd, workloadScoreBd, activityScoreBd, zoneScoreBd,
            distanceKm);
    }

    private static BigDecimal toBd(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Input parameters for the Assignment Score calculation.
     */
    public record ScoreInput(
        double shopperLat,
        double shopperLon,
        double marketLat,
        double marketLon,
        double maxRadiusKm,
        int totalOffers,
        int acceptedOffers,
        int completedOrders,
        double averageRating,
        int activeOrders,
        long minutesSinceLastActivity,
        long daysSinceRegistration
    ) {}
}
