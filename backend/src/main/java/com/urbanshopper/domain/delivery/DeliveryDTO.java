package com.urbanshopper.domain.delivery;

import lombok.Builder;

/**
 * Response DTO for delivery information.
 */
@Builder
public record DeliveryDTO(
    String id,
    String orderId,
    String shopperId,
    String customerId,
    String status,
    String startedAt,
    String etaAt,
    String originalEtaAt,
    String arrivalAt,
    Integer delayMinutes,
    String delayReason,
    String recipientName,
    String recipientRelationship,
    Boolean isAuthorizedRecipient,
    Boolean customerConfirmed,
    String inspectionDeadline,
    String completedAt,
    String notes
) {
    public static DeliveryDTO fromEntity(Delivery d) {
        return DeliveryDTO.builder()
            .id(d.getId().toString())
            .orderId(d.getOrderId().toString())
            .shopperId(d.getShopperId().toString())
            .customerId(d.getCustomerId().toString())
            .status(d.getStatus())
            .startedAt(d.getStartedAt() != null ? d.getStartedAt().toString() : null)
            .etaAt(d.getEtaAt() != null ? d.getEtaAt().toString() : null)
            .originalEtaAt(d.getOriginalEtaAt() != null ? d.getOriginalEtaAt().toString() : null)
            .arrivalAt(d.getArrivalAt() != null ? d.getArrivalAt().toString() : null)
            .delayMinutes(d.getDelayMinutes())
            .delayReason(d.getDelayReason())
            .recipientName(d.getRecipientName())
            .recipientRelationship(d.getRecipientRelationship())
            .isAuthorizedRecipient(d.getIsAuthorizedRecipient())
            .customerConfirmed(d.getCustomerConfirmed())
            .inspectionDeadline(d.getInspectionDeadline() != null ? d.getInspectionDeadline().toString() : null)
            .completedAt(d.getCompletedAt() != null ? d.getCompletedAt().toString() : null)
            .notes(d.getNotes())
            .build();
    }
}
