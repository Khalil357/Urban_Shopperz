package com.urbanshopper.domain.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * M-Pesa API adapter — stub implementation for MVP.
 *
 * TODO: Implement real M-Pesa API integration (Phase 1 Week 11-12).
 * Integration points:
 * - M-Pesa C2B Simulate for pre-auth
 * - M-Pesa Transaction Status for verification
 * - M-Pesa B2C for shopper payout
 */
@Component
public class MpesaAdapter implements PaymentProvider {

    private static final Logger log = LoggerFactory.getLogger(MpesaAdapter.class);

    @Override
    public ProviderResult preAuth(String customerRef, int amount, String reference) {
        log.info("[M-PESA STUB] Pre-auth: customer={}, amount={} TZS, ref={}", customerRef, amount, reference);
        return new ProviderResult(true, "MPESA-STUB-" + reference, "Pre-auth successful (stub)", "{}");
    }

    @Override
    public ProviderResult capture(String providerTransactionRef, int amount, String reference) {
        log.info("[M-PESA STUB] Capture: txRef={}, amount={} TZS, ref={}", providerTransactionRef, amount, reference);
        return new ProviderResult(true, "MPESA-CAP-" + reference, "Capture successful (stub)", "{}");
    }

    @Override
    public ProviderResult refund(String providerTransactionRef, int amount, String reference) {
        log.info("[M-PESA STUB] Refund: txRef={}, amount={} TZS, ref={}", providerTransactionRef, amount, reference);
        return new ProviderResult(true, "MPESA-REF-" + reference, "Refund successful (stub)", "{}");
    }

    @Override
    public ProviderResult payout(String shopperRef, int amount, String reference) {
        log.info("[M-PESA STUB] Payout: shopper={}, amount={} TZS, ref={}", shopperRef, amount, reference);
        return new ProviderResult(true, "MPESA-PAY-" + reference, "Payout successful (stub)", "{}");
    }

    @Override
    public ProviderResult checkStatus(String providerTransactionRef) {
        return new ProviderResult(true, providerTransactionRef, "Transaction completed (stub)", "{}");
    }

    @Override
    public String getProviderName() {
        return "mpesa";
    }
}
