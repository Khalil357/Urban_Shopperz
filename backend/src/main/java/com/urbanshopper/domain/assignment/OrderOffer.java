package com.urbanshopper.domain.assignment;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * OrderOffer — represents a single offer made to a shopper during the assignment cascade.
 *
 * C-003: Each offer has a 30-second acceptance window (expires_at)
 * C-004: Offers cascade through eligible shoppers for up to 3 minutes
 * C-011: Each offer carries the full Assignment Score with component breakdown
 */
@Entity
@Table(name = "order_offers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderOffer {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "shopper_id", nullable = false)
    private UUID shopperId;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "score_distance", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal scoreDistance = BigDecimal.ZERO;

    @Column(name = "score_acceptance", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal scoreAcceptance = BigDecimal.ZERO;

    @Column(name = "score_completion", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal scoreCompletion = BigDecimal.ZERO;

    @Column(name = "score_rating", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal scoreRating = BigDecimal.ZERO;

    @Column(name = "score_workload", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal scoreWorkload = BigDecimal.ZERO;

    @Column(name = "score_activity", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal scoreActivity = BigDecimal.ZERO;

    @Column(name = "score_zone_priority", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal scoreZonePriority = BigDecimal.ZERO;

    @Column(name = "cascade_round", nullable = false)
    @Builder.Default
    private Integer cascadeRound = 1;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "pending";

    @Column(name = "offered_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant offeredAt = Instant.now();

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "responded_at")
    private Instant respondedAt;

    @Column(name = "distance_km", precision = 6, scale = 2)
    private BigDecimal distanceKm;
}
