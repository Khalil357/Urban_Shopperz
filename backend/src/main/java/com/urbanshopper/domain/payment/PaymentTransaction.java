package com.urbanshopper.domain.payment;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * PaymentTransaction — immutable audit record of every financial event.
 *
 * Transaction types: pre_auth, auth_release, capture, refund, payout, settlement
 * Each transaction records the provider interaction for audit and reconciliation.
 */
@Entity
@Table(name = "payment_transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentTransaction {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "transaction_type", nullable = false, length = 30)
    private String transactionType;

    @Column(nullable = false)
    private Integer amount;

    @Column(length = 20)
    private String provider;

    @Column(name = "provider_reference", length = 100)
    private String providerReference;

    @Column(name = "provider_response", columnDefinition = "TEXT")
    private String providerResponse;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "pending";

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
