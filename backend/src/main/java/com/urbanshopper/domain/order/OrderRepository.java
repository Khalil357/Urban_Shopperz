package com.urbanshopper.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
    List<Order> findByShopperIdAndStatusNotIn(UUID shopperId, List<OrderStatus> statuses);
    List<Order> findByStatus(OrderStatus status);
    long countByShopperIdAndStatus(UUID shopperId, OrderStatus status);
    long countByStatus(OrderStatus status);
    long countByCreatedAtAfter(Instant cutoff);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status IN :statuses")
    long countByStatusIn(List<OrderStatus> statuses);
}
