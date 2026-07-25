package com.urbanshopper.domain.order;

/**
 * Result of estimated pricing calculation for an order.
 *
 * Aligns with D-003 (pricing visibility) which requires the customer to see:
 * - estimated item cost
 * - platform service fee (tiered per F-001)
 * - delivery fee (distance-based per F-002)
 * - total estimated cost
 */
public record OrderPricing(
    int estimatedItemCost,
    int serviceFee,
    int deliveryFee,
    int total
) {}
