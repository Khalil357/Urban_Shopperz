package com.urbanshopper.domain.delivery;

import lombok.Builder;

/**
 * Response DTO for GET /api/v1/orders/{orderId}/delivery/eta (E-003).
 */
@Builder
public record DeliveryEtaResponse(
    String orderId,
    String orderStatus,
    String deliveryStatus,
    String etaAt,
    String originalEtaAt,
    String arrivalAt,
    Integer delayMinutes,
    String delayReason
) {}
