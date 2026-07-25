package com.urbanshopper.domain.payment;

/**
 * Payment state machine statuses (coupled to Order State Machine).
 *
 * PENDING    → Pre-auth not yet attempted
 * AUTHORIZED → Pre-auth hold placed on customer (F-003)
 * CAPTURED   → Funds captured on delivery confirmation (F-004)
 * SETTLED    → 48-hour settlement completed (F-006)
 * PARTIAL_REFUNDED → Partial refund issued (G-007)
 * REFUNDED   → Full refund processed (G-006)
 * CANCELLED  → Payment cancelled, hold released (G-001)
 * FAILED     → Payment processing error
 * COMPLETED  → Final state, all financial activity resolved
 */
public enum PaymentStatus {
    PENDING,
    AUTHORIZED,
    CAPTURED,
    SETTLED,
    PARTIAL_REFUNDED,
    REFUNDED,
    CANCELLED,
    FAILED,
    COMPLETED;

    public boolean canTransitionTo(PaymentStatus target) {
        return switch (this) {
            case PENDING -> target == AUTHORIZED || target == CANCELLED || target == FAILED;
            case AUTHORIZED -> target == CAPTURED || target == CANCELLED || target == FAILED;
            case CAPTURED -> target == SETTLED || target == PARTIAL_REFUNDED || target == REFUNDED || target == FAILED;
            case SETTLED -> target == COMPLETED || target == PARTIAL_REFUNDED || target == REFUNDED;
            case PARTIAL_REFUNDED -> target == COMPLETED;
            case REFUNDED -> target == COMPLETED;
            case CANCELLED, FAILED, COMPLETED -> false;
        };
    }
}
