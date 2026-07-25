package com.urbanshopper.domain.order;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * ReceiptPhoto — supporting images attached to a receipt (D-008).
 */
@Entity
@Table(name = "receipt_photos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReceiptPhoto {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "receipt_id", nullable = false)
    private UUID receiptId;

    @Column(name = "photo_url", nullable = false, length = 500)
    private String photoUrl;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
