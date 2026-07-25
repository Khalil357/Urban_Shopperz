package com.urbanshopper.domain.payment;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Payment — financial record for a single order (F-003 through F-006).
 *
 * One payment per order. Tracks the full lifecycle:
 * pre-auth → capture → settlement → completion.
 */
@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "estimated_amount", nullable = false)
    @Builder.Default
    private Integer estimatedAmount = 0;

    @Column(name = "captured_amount")
    private Integer capturedAmount;

    @Column(name = "service_fee")
    private Integer serviceFee;

    @Column(name = "delivery_fee")
    private Integer deliveryFee;

    @Column(name = "item_cost")
    private Integer itemCost;

    @Column(name = "shopper_payout")
    private Integer shopperPayout;

    @Column(name = "shopper_id")
    private UUID shopperId;

    @Column(name = "payment_method", length = 10)
    @Builder.Default
    private String paymentMethod = "mpesa";

    @Column(length = 20)
    private String provider;

    @Column(name = "provider_reference", length = 100)
    private String providerReference;

    @Column(name = "provider_status", length = 50)
    private String providerStatus;

    @Column(name = "authorized_at")
    private Instant authorizedAt;

    @Column(name = "captured_at")
    private Instant capturedAt;

    @Column(name = "settled_at")
    private Instant settledAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate void preUpdate() { this.updatedAt = Instant.now(); }
}
