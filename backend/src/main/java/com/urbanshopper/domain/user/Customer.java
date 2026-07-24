package com.urbanshopper.domain.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers", schema = "user_service")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 15)
    private String phone;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 2)
    @Builder.Default
    private String language = "sw";

    @Column(name = "notification_prefs", columnDefinition = "jsonb")
    @Builder.Default
    private String notificationPrefs = "{\"push\": true, \"sms\": true, \"in_app\": true}";

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "active";

    @Column(name = "trust_score")
    @Builder.Default
    private Integer trustScore = 50;

    @Column(name = "total_orders")
    @Builder.Default
    private Integer totalOrders = 0;

    @Column(name = "last_active_at")
    private Instant lastActiveAt;

    @Column(name = "deactivated_at")
    private Instant deactivatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
