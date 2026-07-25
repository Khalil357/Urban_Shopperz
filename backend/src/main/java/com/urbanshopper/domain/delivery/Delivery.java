package com.urbanshopper.domain.delivery;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Delivery — tracks the physical delivery of an order from market to customer.
 *
 * Each order has exactly one delivery (E-006). Covers:
 * - E-002: Proof of delivery with GPS + photo + authorized recipient
 * - E-003: Dynamic ETA prediction
 * - E-004: Unavailable customer with safe drop or return
 * - E-007: Inspection window deadlines
 * - E-009: Delay reporting and ETA recalculation
 */
@Entity
@Table(name = "deliveries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Delivery {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Column(name = "shopper_id", nullable = false)
    private UUID shopperId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "pending";

    // ── ETA tracking (E-003) ──

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "eta_at")
    private Instant etaAt;

    @Column(name = "original_eta_at")
    private Instant originalEtaAt;

    @Column(name = "arrival_at")
    private Instant arrivalAt;

    @Column(name = "delay_minutes")
    @Builder.Default
    private Integer delayMinutes = 0;

    @Column(name = "delay_reason", columnDefinition = "TEXT")
    private String delayReason;

    @Column(name = "delay_reported_at")
    private Instant delayReportedAt;

    // ── Proof of delivery (E-002) ──

    @Column(name = "proof_lat", precision = 10, scale = 7)
    private BigDecimal proofLat;

    @Column(name = "proof_lng", precision = 10, scale = 7)
    private BigDecimal proofLng;

    @Column(name = "recipient_name", length = 100)
    private String recipientName;

    @Column(name = "recipient_relationship", length = 50)
    private String recipientRelationship;

    @Column(name = "is_authorized_recipient")
    @Builder.Default
    private Boolean isAuthorizedRecipient = false;

    @Column(name = "customer_confirmed")
    @Builder.Default
    private Boolean customerConfirmed = false;

    // ── Inspection window (E-007) ──

    @Column(name = "inspection_deadline")
    private Instant inspectionDeadline;

    @Column(name = "completed_at")
    private Instant completedAt;

    // ── Customer unavailability (E-004) ──

    @Column(name = "unavailable_attempted_at")
    private Instant unavailableAttemptedAt;

    @Column(name = "safe_drop_location", columnDefinition = "TEXT")
    private String safeDropLocation;

    @Column(name = "items_returned")
    @Builder.Default
    private Boolean itemsReturned = false;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // ── Timestamps ──

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate void preUpdate() { this.updatedAt = Instant.now(); }
}
