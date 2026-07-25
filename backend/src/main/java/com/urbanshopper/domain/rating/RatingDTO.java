package com.urbanshopper.domain.rating;

import lombok.Builder;

@Builder
public record RatingDTO(
    String id,
    String orderId,
    String raterId,
    String raterType,
    String rateeId,
    Integer score,
    Integer itemAccuracy,
    Integer itemQuality,
    Integer timeliness,
    Integer communication,
    Integer professionalism,
    String feedback,
    Boolean isRevealed,
    String createdAt
) {
    public static RatingDTO fromEntity(Rating r) {
        return RatingDTO.builder()
            .id(r.getId().toString())
            .orderId(r.getOrderId().toString())
            .raterId(r.getRaterId().toString())
            .raterType(r.getRaterType())
            .rateeId(r.getRateeId().toString())
            .score(r.getScore())
            .itemAccuracy(r.getItemAccuracy())
            .itemQuality(r.getItemQuality())
            .timeliness(r.getTimeliness())
            .communication(r.getCommunication())
            .professionalism(r.getProfessionalism())
            .feedback(r.getFeedback())
            .isRevealed(r.getIsRevealed())
            .createdAt(r.getCreatedAt().toString())
            .build();
    }
}
