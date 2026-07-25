package com.urbanshopper.domain.notification;

/**
 * SMS notification provider abstraction.
 * Implementations: Africa's Talking, Twilio, or local SMS gateway.
 */
public interface SmsProvider {

    SendResult send(String phoneNumber, String message);

    String getProviderName();

    record SendResult(boolean success, String providerReference, String message) {}
}
