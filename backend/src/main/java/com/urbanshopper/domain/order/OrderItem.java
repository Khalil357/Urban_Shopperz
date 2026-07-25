package com.urbanshopper.domain.order;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Column(length = 20)
    private String unit;

    @Column(name = "preferred_brand", length = 100)
    private String preferredBrand;

    @Column(name = "max_price")
    private Integer maxPrice;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "requested";

    @Column(name = "substitution_preference", length = 20)
    private String substitutionPreference;

    @Column(name = "substitution_note", columnDefinition = "TEXT")
    private String substitutionNote;

    @Column(name = "substitution_approval", length = 20)
    private String substitutionApproval;

    @Column(name = "actual_price")
    private Integer actualPrice;

    @Column(name = "has_photo", nullable = false)
    @Builder.Default
    private Boolean hasPhoto = false;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
