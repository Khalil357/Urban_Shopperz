-- V011: Dispute Service — Unified Dispute Resolution Framework
--
-- G-006: Full refund on platform fault
-- G-007: Proportional partial refund tiers
-- G-008: Refund processing timeline
-- G-009: Customer escalation to manual review
-- G-011: Unified dispute lifecycle with evidence and audit trail
-- L-008: Automated triage by value and complexity

-- Disputes — unified resolution for all issue types (G-011)
CREATE TABLE disputes (
    id                  UUID PRIMARY KEY,
    order_id            UUID NOT NULL REFERENCES orders(id),
    dispute_type        VARCHAR(30) NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'reported',

    -- Who filed
    filed_by            UUID NOT NULL,
    filed_by_type       VARCHAR(10) NOT NULL, -- 'customer', 'shopper', 'system'

    -- Against whom
    respondent_id       UUID,
    respondent_type     VARCHAR(10),

    -- Details
    reason              TEXT NOT NULL,
    requested_refund    INTEGER,             -- amount in TZS, if applicable
    assigned_to         UUID,                -- support agent / ops admin

    -- Resolution
    resolution          VARCHAR(30),         -- approved, rejected, partial_refund
    resolution_notes    TEXT,
    refund_amount       INTEGER,
    compensation_amount INTEGER,
    resolved_at         TIMESTAMP,
    closed_at           TIMESTAMP,

    -- SLA tracking
    auto_resolved       BOOLEAN NOT NULL DEFAULT FALSE,
    escalated_at        TIMESTAMP,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_disputes_order ON disputes(order_id);
CREATE INDEX idx_disputes_status ON disputes(status, created_at);
CREATE INDEX idx_disputes_type ON disputes(dispute_type);

-- Dispute evidence — photos, chat logs, receipts (G-011 Step 2)
CREATE TABLE dispute_evidence (
    id                  UUID PRIMARY KEY,
    dispute_id          UUID NOT NULL REFERENCES disputes(id) ON DELETE CASCADE,
    evidence_type       VARCHAR(20) NOT NULL, -- 'photo', 'receipt', 'chat_log', 'gps_log', 'note'
    content             TEXT NOT NULL,         -- URL or text content
    description         VARCHAR(500),
    uploaded_by         UUID,
    uploaded_by_type    VARCHAR(10),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_dispute_evidence ON dispute_evidence(dispute_id);
