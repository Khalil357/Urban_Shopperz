package com.urbanshopper.domain.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShopperWorkHistoryRepository extends JpaRepository<ShopperWorkHistory, UUID> {
    Optional<ShopperWorkHistory> findByShopperIdAndDate(UUID shopperId, LocalDate date);
    List<ShopperWorkHistory> findByShopperIdAndDateBetweenOrderByDateAsc(
        UUID shopperId, LocalDate start, LocalDate end);
}
