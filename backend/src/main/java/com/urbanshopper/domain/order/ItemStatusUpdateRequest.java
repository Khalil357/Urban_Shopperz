package com.urbanshopper.domain.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Request body for POST /api/v1/orders/{orderId}/items/{itemId}/status (D-006).
 *
 * When status is "substituted", additional fields support the substitution workflow (D-007):
 * - substitutionNote: shopper's description of the substitute
 * - substitutionApproval: customer's response (approve/decline/pending)
 * - actualPrice: the price paid for this item
 * - hasPhoto: whether a photo was taken as evidence
 */
@Builder
public record ItemStatusUpdateRequest(
    @NotBlank String status,
    String substitutionNote,
    String substitutionApproval,
    Integer actualPrice,
    Boolean hasPhoto
) {}
