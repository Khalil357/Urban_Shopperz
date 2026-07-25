package com.urbanshopper.domain.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderOfferRepository extends JpaRepository<OrderOffer, UUID> {

    List<OrderOffer> findByOrderIdOrderByCascadeRoundAsc(UUID orderId);

    Optional<OrderOffer> findTopByOrderIdAndStatusOrderByCascadeRoundDesc(
        UUID orderId, String status);

    List<OrderOffer> findByShopperIdAndStatus(UUID shopperId, String status);

    @Query("SELECT o FROM OrderOffer o WHERE o.status = 'pending' AND o.expiresAt < :now")
    List<OrderOffer> findExpiredOffers(Instant now);

    long countByOrderIdAndStatus(UUID orderId, String status);

    Optional<OrderOffer> findTopByOrderIdOrderByCascadeRoundDesc(UUID orderId);
}
