package com.urbanshopper.domain.dispute;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Dispute — unified resolution record for all issue types (G-011).
 *
 * Types: item_discrepancy, shopper_behaviour, payment_failure,
 *        cancellation_fee, high_value, platform_error
 */
@Entity
@Table(name = "disputes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Dispute {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "dispute_type", nullable = false, length = 30)
    private String disputeType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DisputeStatus status = DisputeStatus.REPORTED;

    @Column(name = "filed_by", nullable = false)
    private UUID filedBy;

    @Column(name = "filed_by_type", nullable = false, length = 10)
    private String filedByType;

    @Column(name = "respondent_id")
    private UUID respondentId;

    @Column(name = "respondent_type", length = 10)
    private String respondentType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "requested_refund")
    private Integer requestedRefund;

    @Column(name = "assigned_to")
    private UUID assignedTo;

    @Column(length = 30)
    private String resolution;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "refund_amount")
    private Integer refundAmount;

    @Column(name = "compensation_amount")
    private Integer compensationAmount;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "auto_resolved")
    @Builder.Default
    private Boolean autoResolved = false;

    @Column(name = "escalated_at")
    private Instant escalatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate void preUpdate() { this.updatedAt = Instant.now(); }
}
