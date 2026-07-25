package com.urbanshopper.domain.assignment;

import com.urbanshopper.domain.order.Order;
import lombok.Builder;
import java.math.BigDecimal;

/**
 * DTO for an order offer as seen by the shopper.
 * Only shows general area (C-009) — exact address hidden until acceptance.
 */
@Builder
public record ShopperOfferDTO(
    String id,
    String orderId,
    String status,
    BigDecimal score,
    BigDecimal distanceKm,
    String offeredAt,
    String expiresAt,
    Integer cascadeRound,
    // Order summary (general area only per C-009)
    String zoneName,
    String neighbourhood,
    String marketName,
    Integer itemCount,
    Integer estimatedTotal,
    Integer estimatedDeliveryFee,
    String shoppingPreference
) {
    public static ShopperOfferDTO fromEntity(OrderOffer offer, Order order) {
        return ShopperOfferDTO.builder()
            .id(offer.getId().toString())
            .orderId(order.getId().toString())
            .status(offer.getStatus())
            .score(offer.getScore())
            .distanceKm(offer.getDistanceKm())
            .offeredAt(offer.getOfferedAt().toString())
            .expiresAt(offer.getExpiresAt().toString())
            .cascadeRound(offer.getCascadeRound())
            .zoneName(order.getZoneId().toString()) // Will be replaced with zone name
            .neighbourhood("General area")           // Placeholder per C-009
            .itemCount(order.getItemCount())
            .estimatedTotal(order.getEstimatedTotal())
            .estimatedDeliveryFee(order.getEstimatedDeliveryFee())
            .shoppingPreference(order.getShoppingPreference())
            .build();
    }
}
