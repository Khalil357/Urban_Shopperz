package com.urbanshopper.domain.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ZoneConfigRepository extends JpaRepository<ZoneConfig, UUID> {
}
