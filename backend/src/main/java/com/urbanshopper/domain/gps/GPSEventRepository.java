package com.urbanshopper.domain.gps;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface GPSEventRepository extends JpaRepository<GPSEvent, UUID> {
    List<GPSEvent> findTop20ByShopperIdOrderByRecordedAtDesc(UUID shopperId);
    void deleteByRecordedAtBefore(Instant cutoff);
}
