package com.urbanshopper.domain.delivery;

import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;

/**
 * Request body for POST /api/v1/orders/{orderId}/delivery/confirm (E-002).
 *
 * The shopper confirms delivery with:
 * - GPS coordinates at delivery location
 * - Photo evidence
 * - Optional authorized recipient details
 * - Customer confirmation
 */
@Builder
public record DeliveryConfirmRequest(
    BigDecimal latitude,
    BigDecimal longitude,
    Boolean customerConfirmed,
    String recipientName,
    String recipientRelationship,
    Boolean isAuthorizedRecipient,
    List<String> photoUrls,
    String notes
) {}
