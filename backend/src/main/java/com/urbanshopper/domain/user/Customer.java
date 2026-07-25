package com.urbanshopper.domain.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true, length = 15)
    private String phone;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(length = 2) @Builder.Default
    private String language = "sw";
    @Column(length = 255) @Builder.Default
    private String notificationPrefs = "{\"push\":true,\"sms\":true,\"in_app\":true}";
    @Column(length = 20) @Builder.Default
    private String status = "active";
    @Builder.Default
    private Integer trustScore = 50;
    @Builder.Default
    private Integer totalOrders = 0;
    private Instant lastActiveAt;
    private Instant deactivatedAt;
    @Column(updatable = false) @Builder.Default
    private Instant createdAt = Instant.now();
    @Builder.Default
    private Instant updatedAt = Instant.now();
    @PreUpdate void preUpdate() { this.updatedAt = Instant.now(); }
}
