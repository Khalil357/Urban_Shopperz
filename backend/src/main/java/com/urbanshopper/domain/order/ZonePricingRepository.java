package com.urbanshopper.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ZonePricingRepository extends JpaRepository<ZonePricing, UUID> {
}
