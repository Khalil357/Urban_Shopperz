package com.urbanshopper.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findByOrderIdOrderBySortOrderAsc(UUID orderId);
    long countByOrderIdAndStatusNot(UUID orderId, String status);
}
