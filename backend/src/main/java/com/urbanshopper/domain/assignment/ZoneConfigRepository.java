package com.urbanshopper.domain.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface ZoneConfigRepository extends JpaRepository<ZoneConfig, UUID> {

    /**
     * Update zone fields via native query because ZoneConfig is {@code @Immutable}.
     * Only non-null fields are updated; null fields are left unchanged.
     */
    @Modifying
    @Query(value = """
        UPDATE zones SET
            name = COALESCE(:name, name),
            status = COALESCE(:status, status)
        WHERE id = :id
        """, nativeQuery = true)
    int updateZoneFields(@Param("id") UUID id,
                         @Param("name") String name,
                         @Param("status") String status);
}
