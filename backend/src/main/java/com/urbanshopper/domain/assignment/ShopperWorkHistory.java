package com.urbanshopper.domain.assignment;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Shopper Work History — daily aggregation of assignment metrics (C-011).
 * Used for Assignment Score calculation, performance tiers (B-008), and acceptance rate (B-011).
 */
@Entity
@Table(name = "shopper_work_history",
       uniqueConstraints = @UniqueConstraint(columnNames = {"shopper_id", "date"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ShopperWorkHistory {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "shopper_id", nullable = false)
    private UUID shopperId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "total_offers", nullable = false)
    @Builder.Default
    private Integer totalOffers = 0;

    @Column(name = "accepted_offers", nullable = false)
    @Builder.Default
    private Integer acceptedOffers = 0;

    @Column(name = "completed_orders", nullable = false)
    @Builder.Default
    private Integer completedOrders = 0;

    @Column(name = "cancelled_orders", nullable = false)
    @Builder.Default
    private Integer cancelledOrders = 0;

    @Column(name = "total_earnings", nullable = false)
    @Builder.Default
    private Integer totalEarnings = 0;

    @Column(name = "online_minutes", nullable = false)
    @Builder.Default
    private Integer onlineMinutes = 0;
}
