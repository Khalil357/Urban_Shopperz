package com.urbanshopper.domain.payment;

import lombok.Builder;

@Builder
public record PaymentTransactionDTO(
    String id,
    String paymentId,
    String transactionType,
    Integer amount,
    String provider,
    String providerReference,
    String status,
    String createdAt
) {
    public static PaymentTransactionDTO fromEntity(PaymentTransaction t) {
        return PaymentTransactionDTO.builder()
            .id(t.getId().toString())
            .paymentId(t.getPaymentId().toString())
            .transactionType(t.getTransactionType())
            .amount(t.getAmount())
            .provider(t.getProvider())
            .providerReference(t.getProviderReference())
            .status(t.getStatus())
            .createdAt(t.getCreatedAt().toString())
            .build();
    }
}
