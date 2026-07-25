package com.urbanshopper.domain.order;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_number", nullable = false, unique = true, length = 30)
    private String orderNumber;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "shopper_id")
    private UUID shopperId;

    @Column(name = "market_id")
    private UUID marketId;

    @Column(name = "zone_id", nullable = false)
    private UUID zoneId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private OrderStatus status = OrderStatus.CREATED;

    @Column(name = "shopping_preference", nullable = false, length = 20)
    @Builder.Default
    private String shoppingPreference = "balanced";

    @Column(name = "delivery_preference", nullable = false, length = 10)
    @Builder.Default
    private String deliveryPreference = "asap";

    @Column(name = "scheduled_window_start")
    private Instant scheduledWindowStart;

    @Column(name = "scheduled_window_end")
    private Instant scheduledWindowEnd;

    @Column(name = "payment_method", length = 10)
    @Builder.Default
    private String paymentMethod = "mpesa";

    @Column(name = "delivery_lat", precision = 10, scale = 7)
    private BigDecimal deliveryLat;

    @Column(name = "delivery_lng", precision = 10, scale = 7)
    private BigDecimal deliveryLng;

    @Column(name = "delivery_address_text", columnDefinition = "TEXT")
    private String deliveryAddressText;

    @Column(name = "delivery_landmark", columnDefinition = "TEXT")
    private String deliveryLandmark;

    @Column(name = "estimated_item_cost", nullable = false)
    @Builder.Default
    private Integer estimatedItemCost = 0;

    @Column(name = "estimated_service_fee", nullable = false)
    @Builder.Default
    private Integer estimatedServiceFee = 0;

    @Column(name = "estimated_delivery_fee", nullable = false)
    @Builder.Default
    private Integer estimatedDeliveryFee = 0;

    @Column(name = "estimated_total", nullable = false)
    @Builder.Default
    private Integer estimatedTotal = 0;

    @Column(name = "actual_item_cost")
    private Integer actualItemCost;

    @Column(name = "actual_service_fee")
    private Integer actualServiceFee;

    @Column(name = "actual_delivery_fee")
    private Integer actualDeliveryFee;

    @Column(name = "actual_total")
    private Integer actualTotal;

    @Column(name = "item_count", nullable = false)
    @Builder.Default
    private Integer itemCount = 0;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "cancellation_fee")
    @Builder.Default
    private Integer cancellationFee = 0;

    @Column(name = "cancelled_by", length = 20)
    private String cancelledBy;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "archived_at")
    private Instant archivedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate void preUpdate() { this.updatedAt = Instant.now(); }

    public void transitionTo(OrderStatus target) {
        if (!status.canTransitionTo(target))
            throw new IllegalStateException("Cannot transition from " + status + " to " + target);
        this.status = target;
    }
}
