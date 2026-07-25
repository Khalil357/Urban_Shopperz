package com.urbanshopper.domain.delivery;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Request body for POST /api/v1/orders/{orderId}/delivery/start (E-003).
 * Shopper initiates delivery run to customer.
 */
@Builder
public record DeliveryStartRequest(
    @NotNull Integer estimatedTravelMinutes,
    String notes
) {}
