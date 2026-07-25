package com.urbanshopper.domain.payment;

import lombok.Builder;

@Builder
public record PaymentDTO(
    String id,
    String orderId,
    String customerId,
    String status,
    Integer estimatedAmount,
    Integer capturedAmount,
    Integer serviceFee,
    Integer deliveryFee,
    Integer itemCost,
    Integer shopperPayout,
    String shopperId,
    String paymentMethod,
    String provider,
    String providerReference,
    String authorizedAt,
    String capturedAt,
    String settledAt,
    String createdAt
) {
    public static PaymentDTO fromEntity(Payment p) {
        return PaymentDTO.builder()
            .id(p.getId().toString())
            .orderId(p.getOrderId().toString())
            .customerId(p.getCustomerId().toString())
            .status(p.getStatus().name())
            .estimatedAmount(p.getEstimatedAmount())
            .capturedAmount(p.getCapturedAmount())
            .serviceFee(p.getServiceFee())
            .deliveryFee(p.getDeliveryFee())
            .itemCost(p.getItemCost())
            .shopperPayout(p.getShopperPayout())
            .shopperId(p.getShopperId() != null ? p.getShopperId().toString() : null)
            .paymentMethod(p.getPaymentMethod())
            .provider(p.getProvider())
            .providerReference(p.getProviderReference())
            .authorizedAt(p.getAuthorizedAt() != null ? p.getAuthorizedAt().toString() : null)
            .capturedAt(p.getCapturedAt() != null ? p.getCapturedAt().toString() : null)
            .settledAt(p.getSettledAt() != null ? p.getSettledAt().toString() : null)
            .createdAt(p.getCreatedAt().toString())
            .build();
    }
}
