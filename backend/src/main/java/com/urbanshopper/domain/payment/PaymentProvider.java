package com.urbanshopper.domain.payment;

/**
 * PaymentProvider — abstraction for mobile money integrations.
 * Enables provider-agnostic payment processing (P6 — Provider abstraction principle).
 *
 * Implementations: MpesaAdapter, MixxAdapter, AirtelMoneyAdapter (future)
 *
 * Each method returns a ProviderResult with the outcome.
 */
public interface PaymentProvider {

    /**
     * Place a pre-authorisation hold on the customer's mobile money.
     * The hold reserves funds but does not transfer them.
     *
     * @param customerRef  customer phone or account reference
     * @param amount       amount in TZS
     * @param reference    platform transaction reference
     * @return result with provider reference
     */
    ProviderResult preAuth(String customerRef, int amount, String reference);

    /**
     * Capture funds after delivery confirmation.
     * Completes the transfer from customer to platform.
     *
     * @param providerTransactionRef  reference from preAuth
     * @param amount                  final amount to capture
     * @param reference               platform transaction reference
     * @return result with provider confirmation
     */
    ProviderResult capture(String providerTransactionRef, int amount, String reference);

    /**
     * Refund captured funds to customer.
     *
     * @param providerTransactionRef  reference from capture
     * @param amount                  amount to refund
     * @param reference               platform transaction reference
     * @return result with provider confirmation
     */
    ProviderResult refund(String providerTransactionRef, int amount, String reference);

    /**
     * Payout funds to a shopper's mobile money account.
     *
     * @param shopperRef  shopper's mobile money account reference
     * @param amount      amount in TZS
     * @param reference   platform transaction reference
     * @return result with provider confirmation
     */
    ProviderResult payout(String shopperRef, int amount, String reference);

    /**
     * Check the status of a previous transaction.
     */
    ProviderResult checkStatus(String providerTransactionRef);

    String getProviderName();

    record ProviderResult(boolean success, String providerReference, String message, String rawResponse) {}
}
