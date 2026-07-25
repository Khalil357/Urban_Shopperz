package com.urbanshopper.domain.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DeliveryPhotoRepository extends JpaRepository<DeliveryPhoto, UUID> {
    List<DeliveryPhoto> findByDeliveryIdOrderByCreatedAtAsc(UUID deliveryId);
}
