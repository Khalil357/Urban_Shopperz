package com.urbanshopper.domain.notification;

import java.util.UUID;

/**
 * Push notification provider abstraction.
 * Implementations: Firebase Cloud Messaging (FCM) for production.
 */
public interface PushProvider {

    SendResult send(UUID recipientId, String title, String body, String data);

    String getProviderName();

    record SendResult(boolean success, String providerReference, String message) {}
}
