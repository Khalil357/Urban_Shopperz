package com.urbanshopper.domain.rating;

import lombok.Builder;

/**
 * Aggregated rating summary for a shopper (H-003).
 * Uses recency-weighted or simple average based on count.
 */
@Builder
public record ShopperRatingSummary(
    String shopperId,
    Double averageScore,
    Long totalRatings,
    Long recentRatings,
    Double recentAverage
) {}
