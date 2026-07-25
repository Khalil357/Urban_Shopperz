package com.urbanshopper.domain.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Firebase Cloud Messaging push notification provider — stub for MVP.
 *
 * TODO: Implement real FCM integration with device token lookup.
 * Integration points:
 * - Firebase Admin SDK for push delivery
 * - Device token management per user
 * - Platform-specific payload (Android/iOS)
 */
@Component
public class FcmPushProvider implements PushProvider {

    private static final Logger log = LoggerFactory.getLogger(FcmPushProvider.class);

    @Override
    public SendResult send(UUID recipientId, String title, String body, String data) {
        log.info("[FCM STUB] Push to {}: title='{}', body='{}'", recipientId, title, body);
        return new SendResult(true, "FCM-STUB-" + recipientId.toString().substring(0, 8), "Push sent (stub)");
    }

    @Override
    public String getProviderName() {
        return "fcm";
    }
}
