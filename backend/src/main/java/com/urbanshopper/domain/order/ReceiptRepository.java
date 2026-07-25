package com.urbanshopper.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {
    List<Receipt> findByOrderIdOrderByCreatedAtAsc(UUID orderId);
}
