package com.urbanshopper.domain.dispute;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DisputeRepository extends JpaRepository<Dispute, UUID> {
    Optional<Dispute> findByOrderId(UUID orderId);
    List<Dispute> findByStatusOrderByCreatedAtAsc(DisputeStatus status);
    List<Dispute> findByFiledByOrderByCreatedAtDesc(UUID filedBy);
}
