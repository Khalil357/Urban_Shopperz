-- V006: Assignment Engine — Order Offers & Cascade Tracking
--
-- C-003: 30-second acceptance window per offer
-- C-004: 3-minute maximum offer cascade
-- C-011: Assignment Score with component breakdown
-- B-010: Single active order per shopper

-- Order offers — tracks every offer made during the assignment cascade
CREATE TABLE order_offers (
    id                  UUID PRIMARY KEY,
    order_id            UUID NOT NULL REFERENCES orders(id),
    shopper_id          UUID NOT NULL,
    score               DECIMAL(5,2) NOT NULL,
    score_distance      DECIMAL(5,2) NOT NULL DEFAULT 0,
    score_acceptance    DECIMAL(5,2) NOT NULL DEFAULT 0,
    score_completion    DECIMAL(5,2) NOT NULL DEFAULT 0,
    score_rating        DECIMAL(5,2) NOT NULL DEFAULT 0,
    score_workload      DECIMAL(5,2) NOT NULL DEFAULT 0,
    score_activity      DECIMAL(5,2) NOT NULL DEFAULT 0,
    score_zone_priority DECIMAL(5,2) NOT NULL DEFAULT 0,
    cascade_round       INTEGER NOT NULL DEFAULT 1,
    status              VARCHAR(20) NOT NULL DEFAULT 'pending',
    offered_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at          TIMESTAMP NOT NULL,
    responded_at        TIMESTAMP,
    distance_km         DECIMAL(6,2),

    CONSTRAINT chk_offer_status CHECK (status IN ('pending', 'accepted', 'declined', 'timed_out'))
);

CREATE INDEX idx_offers_order ON order_offers(order_id, cascade_round);
CREATE INDEX idx_offers_shopper ON order_offers(shopper_id, status);
CREATE INDEX idx_offers_pending ON order_offers(status, expires_at);
