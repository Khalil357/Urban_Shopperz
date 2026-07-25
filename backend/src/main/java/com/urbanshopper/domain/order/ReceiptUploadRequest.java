package com.urbanshopper.domain.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import java.util.List;

/**
 * Request body for POST /api/v1/orders/{id}/receipt (D-008).
 *
 * Supports:
 * - Single itemised receipt (receiptType: "photo", "handwritten")
 * - Multiple receipts (multiple entries for different vendors)
 * - No-receipt scenario (receiptType: "manual", with notes)
 */
@Builder
public record ReceiptUploadRequest(
    @NotBlank String receiptType,
    Integer totalAmount,
    String vendorName,
    String notes,
    List<String> photoUrls
) {}
