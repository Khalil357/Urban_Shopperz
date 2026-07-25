package com.urbanshopper.domain.delivery;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * DeliveryPhoto — evidence attached to a delivery (E-002).
 * photo_type: "proof" (delivery proof), "item_condition" (damaged item evidence)
 */
@Entity
@Table(name = "delivery_photos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryPhoto {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "delivery_id", nullable = false)
    private UUID deliveryId;

    @Column(name = "photo_url", nullable = false, length = 500)
    private String photoUrl;

    @Column(name = "photo_type", nullable = false, length = 20)
    @Builder.Default
    private String photoType = "proof";

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
