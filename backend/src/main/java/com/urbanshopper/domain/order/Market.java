package com.urbanshopper.domain.order;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Market — source location where shoppers purchase items (D-001).
 * Read-only reference entity for the order service.
 */
@Entity
@Table(name = "markets")
@Immutable
@Getter
public class Market {

    @Id
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "zone_id", nullable = false)
    private UUID zoneId;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "address_text", columnDefinition = "TEXT")
    private String addressText;

    @Column(length = 20, nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
