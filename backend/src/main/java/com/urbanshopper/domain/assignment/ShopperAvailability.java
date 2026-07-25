package com.urbanshopper.domain.assignment;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Shopper Availability — tracks online/offline state and current location (B-005).
 *
 * A shopper is considered available when:
 * - Status is "online" (manual Go Online)
 * - App heartbeat is being received (configurable timeout, default 2 min)
 * - GPS data is being transmitted at expected frequency
 */
@Entity
@Table(name = "shopper_availabilities")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ShopperAvailability {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "shopper_id", nullable = false, unique = true)
    private UUID shopperId;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "offline";

    @Column(name = "current_lat", precision = 10, scale = 7)
    private BigDecimal currentLat;

    @Column(name = "current_lng", precision = 10, scale = 7)
    private BigDecimal currentLng;

    @Column(name = "current_zone_id")
    private UUID currentZoneId;

    @Column(name = "transport_type", length = 20)
    private String transportType;

    @Column(name = "heartbeat_at")
    private Instant heartbeatAt;

    @Column(name = "online_at")
    private Instant onlineAt;

    @Column(name = "offline_at")
    private Instant offlineAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate void preUpdate() { this.updatedAt = Instant.now(); }
}
