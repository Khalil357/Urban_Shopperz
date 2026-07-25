package com.urbanshopper.domain.order;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "order_status_history")
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderStatusHistory {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private UUID orderId;
    @Column(nullable = false, length = 30)
    private String fromStatus;
    @Column(nullable = false, length = 30)
    private String toStatus;
    @Column(nullable = false, length = 50)
    private String triggerEvent;
    @Column(nullable = false, length = 20)
    private String actorType;
    private UUID actorId;
    private String reason;
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
