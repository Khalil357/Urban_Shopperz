package com.urbanshopper.domain.gps;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * GPS Event — location data ping from the shopper app (C-001).
 *
 * Tiered frequencies based on shopper state:
 * - Online, waiting: every 30s
 * - Offer active: every 10s
 * - Travelling/delivering: every 5s
 * - Shopping: every 15s
 */
@Entity
@Table(name = "gps_events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GPSEvent {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "shopper_id", nullable = false)
    private UUID shopperId;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(precision = 5, scale = 2)
    private BigDecimal speed;

    @Column(precision = 5, scale = 2)
    private BigDecimal bearing;

    @Column(precision = 5, scale = 2)
    private BigDecimal accuracy;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    @Column(name = "received_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant receivedAt = Instant.now();
}
