package com.urbanshopper.domain.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * SMS gateway provider — stub for MVP.
 *
 * TODO: Implement real SMS gateway integration (Africa's Talking, Twilio, etc.).
 */
@Component
public class SmsGatewayProvider implements SmsProvider {

    private static final Logger log = LoggerFactory.getLogger(SmsGatewayProvider.class);

    @Override
    public SendResult send(String phoneNumber, String message) {
        log.info("[SMS STUB] Send to {}: '{}'", phoneNumber, message);
        return new SendResult(true, "SMS-STUB-" + phoneNumber.substring(phoneNumber.length() - 4), "SMS sent (stub)");
    }

    @Override
    public String getProviderName() {
        return "sms_gateway";
    }
}
