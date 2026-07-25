package com.urbanshopper.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
    List<Order> findByShopperIdAndStatusNotIn(UUID shopperId, List<OrderStatus> statuses);
    List<Order> findByStatus(OrderStatus status);
    long countByShopperIdAndStatus(UUID shopperId, OrderStatus status);
}
