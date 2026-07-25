package com.urbanshopper.domain.dispute;

/**
 * Dispute state machine statuses (G-011).
 *
 * REPORTED → UNDER_REVIEW → EVIDENCE_COLLECTION → DECISION → RESOLVED → CLOSED
 *                          ↘ AUTOMATED_VALIDATION ↗           ↗
 *                          ↘ ESCALATED_TO_OPS (complex/high-value)
 */
public enum DisputeStatus {
    REPORTED,
    UNDER_REVIEW,
    EVIDENCE_COLLECTION,
    AUTOMATED_VALIDATION,
    ESCALATED_TO_OPS,
    DECISION,
    RESOLVED,
    CLOSED;

    public boolean canTransitionTo(DisputeStatus target) {
        return switch (this) {
            case REPORTED -> target == UNDER_REVIEW || target == AUTOMATED_VALIDATION
                          || target == ESCALATED_TO_OPS;
            case UNDER_REVIEW -> target == EVIDENCE_COLLECTION || target == AUTOMATED_VALIDATION
                              || target == ESCALATED_TO_OPS || target == DECISION;
            case EVIDENCE_COLLECTION -> target == DECISION || target == RESOLVED;
            case AUTOMATED_VALIDATION -> target == RESOLVED || target == ESCALATED_TO_OPS;
            case ESCALATED_TO_OPS -> target == DECISION;
            case DECISION -> target == RESOLVED;
            case RESOLVED -> target == CLOSED;
            case CLOSED -> false;
        };
    }
}
