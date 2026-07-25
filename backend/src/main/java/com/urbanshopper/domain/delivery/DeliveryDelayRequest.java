package com.urbanshopper.domain.delivery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * Request body for POST /api/v1/orders/{orderId}/delivery/report-delay (E-009).
 */
@Builder
public record DeliveryDelayRequest(
    @NotNull Integer additionalMinutes,
    @NotBlank String reason
) {}
