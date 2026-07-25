package com.urbanshopper.domain.order;

import lombok.Builder;
import java.util.List;

/**
 * DTO for receipt information exposed via API.
 */
@Builder
public record ReceiptDTO(
    String id,
    String orderId,
    String receiptType,
    Integer totalAmount,
    String vendorName,
    String notes,
    Boolean isVerified,
    List<String> photoUrls,
    String createdAt
) {
    public static ReceiptDTO fromEntity(Receipt r) {
        return ReceiptDTO.builder()
            .id(r.getId().toString())
            .orderId(r.getOrderId().toString())
            .receiptType(r.getReceiptType())
            .totalAmount(r.getTotalAmount())
            .vendorName(r.getVendorName())
            .notes(r.getNotes())
            .isVerified(r.getIsVerified())
            .createdAt(r.getCreatedAt().toString())
            .build();
    }
}
