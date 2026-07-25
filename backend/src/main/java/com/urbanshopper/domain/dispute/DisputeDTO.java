package com.urbanshopper.domain.dispute;

import lombok.Builder;

@Builder
public record DisputeDTO(
    String id,
    String orderId,
    String disputeType,
    String status,
    String filedBy,
    String filedByType,
    String respondentId,
    String respondentType,
    String reason,
    Integer requestedRefund,
    String assignedTo,
    String resolution,
    String resolutionNotes,
    Integer refundAmount,
    Integer compensationAmount,
    Boolean autoResolved,
    String resolvedAt,
    String closedAt,
    String createdAt
) {
    public static DisputeDTO fromEntity(Dispute d) {
        return DisputeDTO.builder()
            .id(d.getId().toString())
            .orderId(d.getOrderId().toString())
            .disputeType(d.getDisputeType())
            .status(d.getStatus().name())
            .filedBy(d.getFiledBy().toString())
            .filedByType(d.getFiledByType())
            .respondentId(d.getRespondentId() != null ? d.getRespondentId().toString() : null)
            .respondentType(d.getRespondentType())
            .reason(d.getReason())
            .requestedRefund(d.getRequestedRefund())
            .assignedTo(d.getAssignedTo() != null ? d.getAssignedTo().toString() : null)
            .resolution(d.getResolution())
            .resolutionNotes(d.getResolutionNotes())
            .refundAmount(d.getRefundAmount())
            .compensationAmount(d.getCompensationAmount())
            .autoResolved(d.getAutoResolved())
            .resolvedAt(d.getResolvedAt() != null ? d.getResolvedAt().toString() : null)
            .closedAt(d.getClosedAt() != null ? d.getClosedAt().toString() : null)
            .createdAt(d.getCreatedAt().toString())
            .build();
    }
}
