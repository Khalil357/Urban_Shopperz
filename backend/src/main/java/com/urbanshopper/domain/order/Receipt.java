package com.urbanshopper.domain.order;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Receipt — proof of purchase for an order (D-008).
 * Supports multiple formats: photo, handwritten, manual entry.
 * An order may have multiple receipts (multiple vendors within same market).
 */
@Entity
@Table(name = "receipts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Receipt {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "receipt_type", nullable = false, length = 20)
    @Builder.Default
    private String receiptType = "photo";

    @Column(name = "total_amount")
    private Integer totalAmount;

    @Column(name = "vendor_name", length = 200)
    private String vendorName;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
