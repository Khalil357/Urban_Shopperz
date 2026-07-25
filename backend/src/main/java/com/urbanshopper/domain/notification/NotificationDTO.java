package com.urbanshopper.domain.notification;

import lombok.Builder;

@Builder
public record NotificationDTO(
    String id,
    String recipientId,
    String recipientType,
    String channel,
    String templateKey,
    String title,
    String body,
    String data,
    String status,
    Boolean isRead,
    String sentAt,
    String readAt,
    String createdAt
) {
    public static NotificationDTO fromEntity(Notification n) {
        return NotificationDTO.builder()
            .id(n.getId().toString())
            .recipientId(n.getRecipientId().toString())
            .recipientType(n.getRecipientType())
            .channel(n.getChannel())
            .templateKey(n.getTemplateKey())
            .title(n.getTitle())
            .body(n.getBody())
            .data(n.getData())
            .status(n.getStatus())
            .isRead(n.getIsRead())
            .sentAt(n.getSentAt() != null ? n.getSentAt().toString() : null)
            .readAt(n.getReadAt() != null ? n.getReadAt().toString() : null)
            .createdAt(n.getCreatedAt().toString())
            .build();
    }
}
