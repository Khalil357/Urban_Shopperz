package com.urbanshopper.domain.payment;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * ShopperWallet — tracks earnings for payout management (F-010).
 *
 * - available_balance: settled earnings ready for withdrawal
 * - pending_balance: earnings from orders not yet settled (48h window)
 * - lifetime_earnings: cumulative total for performance tracking
 */
@Entity
@Table(name = "shopper_wallets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ShopperWallet {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "shopper_id", nullable = false, unique = true)
    private UUID shopperId;

    @Column(name = "available_balance", nullable = false)
    @Builder.Default
    private Integer availableBalance = 0;

    @Column(name = "pending_balance", nullable = false)
    @Builder.Default
    private Integer pendingBalance = 0;

    @Column(name = "lifetime_earnings", nullable = false)
    @Builder.Default
    private Integer lifetimeEarnings = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate void preUpdate() { this.updatedAt = Instant.now(); }
}
