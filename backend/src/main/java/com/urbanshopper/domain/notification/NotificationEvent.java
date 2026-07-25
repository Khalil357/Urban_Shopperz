package com.urbanshopper.domain.notification;

import java.util.Map;
import java.util.UUID;

/**
 * Generic notification event for triggering notifications from any domain.
 * Published by domain services and consumed by NotificationService.
 */
public record NotificationEvent(
    UUID recipientId,
    String recipientType,
    String templateKey,
    String channel,
    String title,
    String body,
    Map<String, String> variables,
    String data
) {
    public NotificationEvent(UUID recipientId, String recipientType, String templateKey,
                             Map<String, String> variables, String data) {
        this(recipientId, recipientType, templateKey, "in_app", null, null, variables, data);
    }
}
