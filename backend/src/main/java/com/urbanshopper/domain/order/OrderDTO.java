package com.urbanshopper.domain.order;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record OrderDTO(
    String id,
    String orderNumber,
    String customerId,
    String shopperId,
    String marketId,
    String zoneId,
    String status,
    String shoppingPreference,
    String deliveryPreference,
    String scheduledWindowStart,
    String scheduledWindowEnd,
    String paymentMethod,
    BigDecimal deliveryLat,
    BigDecimal deliveryLng,
    String deliveryAddressText,
    String deliveryLandmark,
    Integer estimatedItemCost,
    Integer estimatedServiceFee,
    Integer estimatedDeliveryFee,
    Integer estimatedTotal,
    Integer actualItemCost,
    Integer actualServiceFee,
    Integer actualDeliveryFee,
    Integer actualTotal,
    Integer itemCount,
    String cancellationReason,
    Integer cancellationFee,
    String cancelledBy,
    String cancelledAt,
    String archivedAt,
    String createdAt,
    String updatedAt
) {
    public static OrderDTO fromEntity(Order o) {
        return OrderDTO.builder()
            .id(o.getId().toString())
            .orderNumber(o.getOrderNumber())
            .customerId(o.getCustomerId().toString())
            .shopperId(o.getShopperId() != null ? o.getShopperId().toString() : null)
            .marketId(o.getMarketId() != null ? o.getMarketId().toString() : null)
            .zoneId(o.getZoneId().toString())
            .status(o.getStatus().name())
            .shoppingPreference(o.getShoppingPreference())
            .deliveryPreference(o.getDeliveryPreference())
            .scheduledWindowStart(o.getScheduledWindowStart() != null ? o.getScheduledWindowStart().toString() : null)
            .scheduledWindowEnd(o.getScheduledWindowEnd() != null ? o.getScheduledWindowEnd().toString() : null)
            .paymentMethod(o.getPaymentMethod())
            .deliveryLat(o.getDeliveryLat())
            .deliveryLng(o.getDeliveryLng())
            .deliveryAddressText(o.getDeliveryAddressText())
            .deliveryLandmark(o.getDeliveryLandmark())
            .estimatedItemCost(o.getEstimatedItemCost())
            .estimatedServiceFee(o.getEstimatedServiceFee())
            .estimatedDeliveryFee(o.getEstimatedDeliveryFee())
            .estimatedTotal(o.getEstimatedTotal())
            .actualItemCost(o.getActualItemCost())
            .actualServiceFee(o.getActualServiceFee())
            .actualDeliveryFee(o.getActualDeliveryFee())
            .actualTotal(o.getActualTotal())
            .itemCount(o.getItemCount())
            .cancellationReason(o.getCancellationReason())
            .cancellationFee(o.getCancellationFee())
            .cancelledBy(o.getCancelledBy())
            .cancelledAt(o.getCancelledAt() != null ? o.getCancelledAt().toString() : null)
            .archivedAt(o.getArchivedAt() != null ? o.getArchivedAt().toString() : null)
            .createdAt(o.getCreatedAt().toString())
            .updatedAt(o.getUpdatedAt().toString())
            .build();
    }
}
