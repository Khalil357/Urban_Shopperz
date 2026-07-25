package com.urbanshopper.domain.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {
    Optional<Rating> findByOrderIdAndRaterType(UUID orderId, String raterType);
    List<Rating> findByRateeIdAndRaterTypeOrderByCreatedAtDesc(UUID rateeId, String raterType);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.rateeId = :rateeId AND r.raterType = 'customer'")
    long countByRateeId(UUID rateeId);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.rateeId = :rateeId AND r.raterType = 'customer' AND r.isFlagged = false")
    Optional<Double> averageScoreByRateeId(UUID rateeId);

    List<Rating> findByOrderId(UUID orderId);
}
