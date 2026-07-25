package com.urbanshopper.domain.rating;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Rating — two-way feedback between customers and shoppers (H-001, H-002).
 *
 * One rating per order per role (customer rates shopper, shopper rates customer).
 * Blind period (H-006): ratings hidden until both parties submit or 72 hours pass.
 */
@Entity
@Table(name = "ratings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Rating {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Column(name = "rater_id", nullable = false)
    private UUID raterId;

    @Column(name = "rater_type", nullable = false, length = 10)
    private String raterType;

    @Column(name = "ratee_id", nullable = false)
    private UUID rateeId;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "item_accuracy")
    private Integer itemAccuracy;

    @Column(name = "item_quality")
    private Integer itemQuality;

    @Column
    private Integer timeliness;

    @Column
    private Integer communication;

    @Column
    private Integer professionalism;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "is_revealed", nullable = false)
    @Builder.Default
    private Boolean isRevealed = false;

    @Column(name = "is_flagged", nullable = false)
    @Builder.Default
    private Boolean isFlagged = false;

    @Column(name = "flag_reason", length = 100)
    private String flagReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
