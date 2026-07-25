package com.urbanshopper.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MarketRepository extends JpaRepository<Market, UUID> {
    List<Market> findByZoneIdAndStatus(UUID zoneId, String status);
}
