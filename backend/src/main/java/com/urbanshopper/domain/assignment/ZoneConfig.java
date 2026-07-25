package com.urbanshopper.domain.assignment;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Read-only zone configuration for the Assignment Engine.
 * Maps to the shared `zones` table to read assignment parameters:
 * - max_assignment_radius_km (C-005)
 * - center coordinates for distance calculations
 */
@Entity
@Table(name = "zones")
@Immutable
@Getter
public class ZoneConfig {

    @Id
    private UUID id;

    @Column(length = 100)
    private String name;

    @Column(name = "max_assignment_radius_km", precision = 5, scale = 2)
    private BigDecimal maxAssignmentRadiusKm;

    @Column(name = "center_lat", precision = 10, scale = 7)
    private BigDecimal centerLat;

    @Column(name = "center_lng", precision = 10, scale = 7)
    private BigDecimal centerLng;

    @Column(length = 20)
    private String status;
}
