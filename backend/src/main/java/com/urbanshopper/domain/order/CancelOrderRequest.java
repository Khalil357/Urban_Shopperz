package com.urbanshopper.domain.order;

import lombok.Builder;

/**
 * Request body for POST /api/v1/orders/{id}/cancel
 */
@Builder
public record CancelOrderRequest(String reason) {}
