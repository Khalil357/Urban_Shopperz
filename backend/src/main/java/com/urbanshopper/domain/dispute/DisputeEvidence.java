package com.urbanshopper.domain.dispute;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * DisputeEvidence — evidence attached to a dispute (G-011 Step 2).
 * Types: photo, receipt, chat_log, gps_log, note
 */
@Entity
@Table(name = "dispute_evidence")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DisputeEvidence {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "dispute_id", nullable = false)
    private UUID disputeId;

    @Column(name = "evidence_type", nullable = false, length = 20)
    private String evidenceType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 500)
    private String description;

    @Column(name = "uploaded_by")
    private UUID uploadedBy;

    @Column(name = "uploaded_by_type", length = 10)
    private String uploadedByType;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
