package com.urbanshopper.domain.order;

import com.urbanshopper.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * Pricing calculation engine implementing:
 * - F-001: Tiered platform service fee (8/10/12/15%, min 1,000 TZS)
 * - F-002: Zone-based delivery fee (base fee + per-km rate)
 * - D-003: 10% / 10,000 TZS variance threshold check
 */
@Service
@RequiredArgsConstructor
public class OrderPricingService {

    private static final int TIER_1_LIMIT = 50_000;     // 8%
    private static final int TIER_2_LIMIT = 150_000;    // 10%
    private static final int TIER_3_LIMIT = 300_000;    // 12%
    private static final double TIER_1_RATE = 0.08;
    private static final double TIER_2_RATE = 0.10;
    private static final double TIER_3_RATE = 0.12;
    private static final double TIER_4_RATE = 0.15;
    private static final int MIN_SERVICE_FEE = 1_000;

    private static final double VARIANCE_THRESHOLD_PERCENT = 0.10;  // 10%
    private static final int VARIANCE_THRESHOLD_AMOUNT = 10_000;     // 10,000 TZS

    private final ZonePricingRepository zonePricingRepository;

    /**
     * Calculate estimated pricing for an order.
     * Called during order creation (D-003).
     */
    public OrderPricing calculateEstimatedPricing(CreateOrderRequest req) {
        // Calculate estimated item cost from item max prices (D-002)
        int estimatedItemCost = req.items().stream()
            .mapToInt(item -> {
                int price = item.maxPrice() != null ? item.maxPrice() : estimateDefaultPrice(item.name());
                return price * item.quantity();
            })
            .sum();

        if (estimatedItemCost <= 0) {
            estimatedItemCost = req.items().size() * 5_000; // fallback: 5,000 TZS per item
        }

        int serviceFee = calculateServiceFee(estimatedItemCost);
        int deliveryFee = calculateDeliveryFee(req.zoneId());

        return new OrderPricing(estimatedItemCost, serviceFee, deliveryFee,
            estimatedItemCost + serviceFee + deliveryFee);
    }

    /**
     * F-001: Tiered platform service fee.
     *
     * 0 — 50,000 TZS:      8%
     * 50,001 — 150,000:   10%
     * 150,001 — 300,000:  12%
     * Above 300,000:      15%
     * Minimum: 1,000 TZS
     */
    public int calculateServiceFee(int itemCost) {
        double rate;
        if (itemCost <= TIER_1_LIMIT) {
            rate = TIER_1_RATE;
        } else if (itemCost <= TIER_2_LIMIT) {
            rate = TIER_2_RATE;
        } else if (itemCost <= TIER_3_LIMIT) {
            rate = TIER_3_RATE;
        } else {
            rate = TIER_4_RATE;
        }
        int fee = (int) Math.round(itemCost * rate);
        return Math.max(fee, MIN_SERVICE_FEE);
    }

    /**
     * F-002: Zone-based delivery fee.
     * Uses the zone's base_delivery_fee as the minimum charge.
     * Per-km rate calculation deferred until Assignment Engine provides road distance.
     */
    public int calculateDeliveryFee(UUID zoneId) {
        var zone = zonePricingRepository.findById(zoneId)
            .orElseThrow(() -> new BusinessException("ZONE_NOT_FOUND",
                "Zone not found: " + zoneId));
        return zone.getBaseDeliveryFee() != null ? zone.getBaseDeliveryFee() : 1_500;
    }

    /**
     * D-003: Check if final cost exceeds estimate by more than 10% OR 10,000 TZS.
     * Whichever occurs first.
     */
    public boolean requiresVarianceApproval(int estimatedTotal, int actualTotal) {
        if (estimatedTotal <= 0) return false;
        int difference = Math.abs(actualTotal - estimatedTotal);
        double percentDiff = (double) difference / estimatedTotal;
        return percentDiff > VARIANCE_THRESHOLD_PERCENT || difference > VARIANCE_THRESHOLD_AMOUNT;
    }

    /**
     * Default price estimation when no maxPrice is provided.
     * Simple heuristic based on item name keywords.
     */
    private int estimateDefaultPrice(String itemName) {
        var lower = itemName.toLowerCase();
        if (lower.contains("rice") || lower.contains("sugar") || lower.contains("cooking oil")) {
            return 5_000;
        } else if (lower.contains("meat") || lower.contains("fish") || lower.contains("chicken")) {
            return 12_000;
        } else if (lower.contains("milk") || lower.contains("bread") || lower.contains("eggs")) {
            return 3_000;
        } else if (lower.contains("soap") || lower.contains("detergent")) {
            return 4_000;
        }
        return 3_000; // default fallback
    }
}
