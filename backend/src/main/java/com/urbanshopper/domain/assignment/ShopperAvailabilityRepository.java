package com.urbanshopper.domain.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShopperAvailabilityRepository extends JpaRepository<ShopperAvailability, UUID> {
    Optional<ShopperAvailability> findByShopperId(UUID shopperId);
    List<ShopperAvailability> findByStatusAndCurrentZoneId(String status, UUID currentZoneId);
    List<ShopperAvailability> findByStatus(String status);
    long countByStatus(String status);
}
