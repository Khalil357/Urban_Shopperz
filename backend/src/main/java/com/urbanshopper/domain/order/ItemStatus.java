package com.urbanshopper.domain.order;

/**
 * Status of an individual order item during the shopping flow (D-006).
 *
 * REQUESTED     — default, not yet processed
 * FOUND         — purchased as requested
 * SUBSTITUTED   — replaced with a similar item (may need customer approval per D-007)
 * PENDING_APPROVAL — substitution awaiting customer approval (Contact Me workflow)
 * NOT_AVAILABLE — item could not be found in the market
 */
public enum ItemStatus {
    REQUESTED,
    FOUND,
    SUBSTITUTED,
    PENDING_APPROVAL,
    NOT_AVAILABLE;

    public boolean isTerminal() {
        return this == FOUND || this == NOT_AVAILABLE;
    }
}
