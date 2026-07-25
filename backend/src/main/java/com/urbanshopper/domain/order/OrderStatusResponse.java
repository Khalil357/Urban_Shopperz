package com.urbanshopper.domain.order;

import lombok.Builder;
import java.util.List;

/**
 * Response DTO for GET /api/v1/orders/{id}/status.
 * Provides a comprehensive status summary with item counts and timeline.
 */
@Builder
public record OrderStatusResponse(
    String orderId,
    String orderNumber,
    String status,
    Integer totalItems,
    Integer itemsFound,
    Integer itemsSubstituted,
    Integer itemsUnavailable,
    Integer itemsPending,
    Integer estimatedTotal,
    List<TimelineEvent> timeline
) {
    @Builder
    public record TimelineEvent(
        String fromStatus,
        String toStatus,
        String triggerEvent,
        String timestamp
    ) {}
}
