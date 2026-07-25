package com.urbanshopper.domain.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Mixx API adapter — fallback payment provider stub.
 *
 * TODO: Implement real Mixx API integration when M-Pesa fallback is needed.
 */
@Component
public class MixxAdapter implements PaymentProvider {

    private static final Logger log = LoggerFactory.getLogger(MixxAdapter.class);

    @Override
    public ProviderResult preAuth(String customerRef, int amount, String reference) {
        log.info("[MIXX STUB] Pre-auth: customer={}, amount={} TZS, ref={}", customerRef, amount, reference);
        return new ProviderResult(true, "MIXX-STUB-" + reference, "Pre-auth successful (stub)", "{}");
    }

    @Override
    public ProviderResult capture(String providerTransactionRef, int amount, String reference) {
        log.info("[MIXX STUB] Capture: txRef={}, amount={} TZS, ref={}", providerTransactionRef, amount, reference);
        return new ProviderResult(true, "MIXX-CAP-" + reference, "Capture successful (stub)", "{}");
    }

    @Override
    public ProviderResult refund(String providerTransactionRef, int amount, String reference) {
        return new ProviderResult(true, "MIXX-REF-" + reference, "Refund successful (stub)", "{}");
    }

    @Override
    public ProviderResult payout(String shopperRef, int amount, String reference) {
        return new ProviderResult(true, "MIXX-PAY-" + reference, "Payout successful (stub)", "{}");
    }

    @Override
    public ProviderResult checkStatus(String providerTransactionRef) {
        return new ProviderResult(true, providerTransactionRef, "Transaction completed (stub)", "{}");
    }

    @Override
    public String getProviderName() {
        return "mixx";
    }
}
