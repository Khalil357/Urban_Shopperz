package com.urbanshopper.domain.assignment;

import java.math.BigDecimal;

/**
 * Result of an Assignment Score calculation (C-011).
 * Contains the total score and all component scores for audit/transparency.
 */
public record AssignmentScore(
    BigDecimal total,
    BigDecimal distance,
    BigDecimal acceptanceRate,
    BigDecimal completionRate,
    BigDecimal rating,
    BigDecimal workload,
    BigDecimal activity,
    BigDecimal zonePriority,
    double distanceKm
) {
    /**
     * Create a zero score (default for new shoppers with no history).
     */
    public static AssignmentScore zero(double distanceKm) {
        return new AssignmentScore(
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
            distanceKm);
    }
}
