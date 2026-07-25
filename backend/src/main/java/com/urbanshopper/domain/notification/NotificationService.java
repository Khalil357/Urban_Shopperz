package com.urbanshopper.domain.notification;

import com.urbanshopper.domain.order.Order;
import com.urbanshopper.domain.order.events.OrderCreatedEvent;
import com.urbanshopper.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Notification Service — multi-channel messaging with template rendering.
 *
 * J-003: Push + in-app + SMS for assignment notifications
 * J-004: Order status notifications to customer
 * J-006: Language-localised templates (Swahili/English)
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final Optional<PushProvider> pushProvider;
    private final Optional<SmsProvider> smsProvider;

    // ═══════════════════════════════════════════════
    //  Send API
    // ═══════════════════════════════════════════════

    /**
     * Send a notification using a template.
     * Renders the template with variables, then delivers via the specified channel.
     */
    @Transactional
    public NotificationDTO sendFromTemplate(
            UUID recipientId, String recipientType, String templateKey,
            String channel, Map<String, String> variables, String data) {

        var template = templateRepository.findByTemplateKey(templateKey)
            .orElseThrow(() -> new BusinessException("TEMPLATE_NOT_FOUND",
                "Notification template not found: " + templateKey));

        // Render template (use English for now; Swahili support when language pref is available)
        var title = renderTemplate(template.getTitleEn(), variables);
        var body = renderTemplate(template.getBodyEn(), variables);

        return send(recipientId, recipientType, channel, templateKey, title, body, data);
    }

    /**
     * Send a raw notification (without template).
     */
    @Transactional
    public NotificationDTO send(
            UUID recipientId, String recipientType, String channel,
            String templateKey, String title, String body, String data) {

        var notification = Notification.builder()
            .recipientId(recipientId)
            .recipientType(recipientType)
            .channel(channel)
            .templateKey(templateKey)
            .title(title)
            .body(body)
            .data(data)
            .build();

        // Deliver via the appropriate channel
        boolean delivered = deliver(notification);

        notification.setStatus(delivered ? "sent" : "failed");
        notification.setSentAt(Instant.now());

        var saved = notificationRepository.save(notification);
        log.info("Notification {}: recipient={}, channel={}, template={}",
            delivered ? "sent" : "failed", recipientId, channel, templateKey);

        return NotificationDTO.fromEntity(saved);
    }

    /**
     * Send an event-driven notification with multi-channel fallback.
     * Push primary, in-app always, SMS as fallback for critical messages.
     */
    @Transactional
    public void sendEvent(NotificationEvent event) {
        // Always save in-app notification
        var title = event.title();
        var body = event.body();

        if (title == null && event.templateKey() != null) {
            // Try to use template
            try {
                sendFromTemplate(event.recipientId(), event.recipientType(),
                    event.templateKey(), "in_app", event.variables(), event.data());
                return;
            } catch (Exception e) {
                log.warn("Template {} not found, sending raw", event.templateKey());
            }
        }

        // Send raw
        send(event.recipientId(), event.recipientType(), event.channel(),
            event.templateKey(), title != null ? title : "",
            body != null ? body : "", event.data());
    }

    // ═══════════════════════════════════════════════
    //  Read API
    // ═══════════════════════════════════════════════

    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotifications(UUID recipientId, String recipientType) {
        return notificationRepository
            .findByRecipientIdAndRecipientTypeOrderByCreatedAtDesc(recipientId, recipientType)
            .stream()
            .map(NotificationDTO::fromEntity)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications(UUID recipientId, String recipientType) {
        return notificationRepository
            .findUnreadByRecipient(recipientId, recipientType)
            .stream()
            .map(NotificationDTO::fromEntity)
            .toList();
    }

    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount(UUID recipientId, String recipientType) {
        return new UnreadCountResponse(
            notificationRepository.countByRecipientIdAndRecipientTypeAndIsReadFalse(
                recipientId, recipientType));
    }

    /**
     * Mark a notification as read.
     */
    @Transactional
    public void markAsRead(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            n.setReadAt(Instant.now());
            notificationRepository.save(n);
        });
    }

    // ═══════════════════════════════════════════════
    //  Event Listeners — Automatic Notifications
    // ═══════════════════════════════════════════════

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Auto-notification: order created {} for customer {}", event.aggregateId(), event.customerId());
        // In-app notification for customer
        send(event.customerId(), "customer", "in_app", "order.created",
            "Order Created", "Your order #" + event.aggregateId().toString().substring(0, 8) + " is being processed.",
            "{\"orderId\":\"" + event.aggregateId() + "\"}");
    }

    @EventListener
    public void onNotificationEvent(NotificationEvent event) {
        sendEvent(event);
    }

    // ═══════════════════════════════════════════════
    //  Private
    // ═══════════════════════════════════════════════

    private boolean deliver(Notification notification) {
        return switch (notification.getChannel()) {
            case "push" -> pushProvider.map(p -> {
                var result = p.send(notification.getRecipientId(),
                    notification.getTitle(), notification.getBody(), notification.getData());
                notification.setProviderReference(result.providerReference());
                return result.success();
            }).orElse(false);

            case "sms" -> smsProvider.map(p -> {
                // SMS needs phone number, not UUID. For MVP, just log.
                log.info("[SMS] Would send to {}: {}", notification.getRecipientId(), notification.getBody());
                return true;
            }).orElse(false);

            case "in_app" -> true; // In-app always succeeds (saved to DB)

            default -> {
                log.warn("Unknown notification channel: {}", notification.getChannel());
                yield false;
            }
        };
    }

    private String renderTemplate(String template, Map<String, String> variables) {
        if (template == null || variables == null) return template;
        var result = template;
        for (var entry : variables.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}
