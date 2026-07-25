-- V009: Rating Service — Two-way ratings with blind period and fraud detection
--
-- H-001: Customer rates shopper (1-5 stars, criteria breakdown, 72hr window)
-- H-002: Shopper rates customer privately (1-5 stars, internal only)
-- H-003: Recency-weighted rating calculation
-- H-005: Rating fraud detection foundation
-- H-006: Blind period — ratings hidden until both submitted or 72hrs pass

CREATE TABLE ratings (
    id                  UUID PRIMARY KEY,
    order_id            UUID NOT NULL UNIQUE REFERENCES orders(id),

    rater_id            UUID NOT NULL,
    rater_type          VARCHAR(10) NOT NULL CHECK (rater_type IN ('customer', 'shopper')),
    ratee_id            UUID NOT NULL,

    -- Overall score (1-5)
    score               INTEGER NOT NULL CHECK (score >= 1 AND score <= 5),

    -- Criteria breakdown (optional, for customer→shopper ratings)
    item_accuracy       INTEGER CHECK (item_accuracy >= 1 AND item_accuracy <= 5),
    item_quality        INTEGER CHECK (item_quality >= 1 AND item_quality <= 5),
    timeliness          INTEGER CHECK (timeliness >= 1 AND timeliness <= 5),
    communication       INTEGER CHECK (communication >= 1 AND communication <= 5),
    professionalism     INTEGER CHECK (professionalism >= 1 AND professionalism <= 5),

    -- Written feedback (H-001)
    feedback            TEXT,

    -- Blind period (H-006): visible to other party only when both submit or 72hrs pass
    is_revealed         BOOLEAN NOT NULL DEFAULT FALSE,

    -- Fraud detection (H-005)
    is_flagged          BOOLEAN NOT NULL DEFAULT FALSE,
    flag_reason         VARCHAR(100),

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ratings_ratee ON ratings(ratee_id, created_at DESC);
CREATE INDEX idx_ratings_order ON ratings(order_id);
CREATE INDEX idx_ratings_rater ON ratings(rater_id);
