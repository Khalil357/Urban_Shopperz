package com.urbanshopper.domain.order;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import java.util.UUID;

/**
 * Read-only JPA entity for zone pricing configuration.
 * Maps to the shared `zones` table (V001 migration) for the Order Service's pricing needs.
 *
 * In a microservices future, each service maintains its own copy of reference data.
 * This is the Order Service's local projection.
 */
@Entity
@Table(name = "zones")
@Immutable
@Getter
public class ZonePricing {

    @Id
    private UUID id;

    @Column(name = "base_delivery_fee")
    private Integer baseDeliveryFee;

    @Column(name = "per_km_rate")
    private Integer perKmRate;

    @Column(length = 20)
    private String status;
}
