-- V005: Delivery Flow — Tracking, Proof of Delivery, ETA
--
-- E-002: Proof of delivery with GPS verification and photo evidence
-- E-003: Dynamic ETA prediction and tracking
-- E-004: Unavailable customer procedure
-- E-007: Category-based inspection windows
-- E-009: ETA recalculation and delay reporting

-- Deliveries — one per order, tracks the physical delivery of goods
CREATE TABLE deliveries (
    id                  UUID PRIMARY KEY,
    order_id            UUID NOT NULL UNIQUE REFERENCES orders(id),
    shopper_id          UUID NOT NULL,
    customer_id         UUID NOT NULL,
    status              VARCHAR(30) NOT NULL DEFAULT 'pending',

    -- ETA tracking (E-003)
    started_at          TIMESTAMP,
    eta_at              TIMESTAMP,
    original_eta_at     TIMESTAMP,
    arrival_at          TIMESTAMP,
    delay_minutes       INTEGER NOT NULL DEFAULT 0,
    delay_reason        TEXT,
    delay_reported_at   TIMESTAMP,

    -- Proof of delivery (E-002)
    proof_lat           DECIMAL(10,7),
    proof_lng           DECIMAL(10,7),
    recipient_name      VARCHAR(100),
    recipient_relationship VARCHAR(50),
    is_authorized_recipient BOOLEAN NOT NULL DEFAULT FALSE,
    customer_confirmed  BOOLEAN NOT NULL DEFAULT FALSE,

    -- Inspection window (E-007)
    inspection_deadline TIMESTAMP,
    completed_at        TIMESTAMP,

    -- Customer unavailability (E-004)
    unavailable_attempted_at TIMESTAMP,
    safe_drop_location  TEXT,
    items_returned      BOOLEAN NOT NULL DEFAULT FALSE,

    notes               TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_deliveries_order ON deliveries(order_id);
CREATE INDEX idx_deliveries_shopper ON deliveries(shopper_id);
CREATE INDEX idx_deliveries_status ON deliveries(status);

-- Delivery photos — evidence of delivery (proof photo, item condition)
CREATE TABLE delivery_photos (
    id              UUID PRIMARY KEY,
    delivery_id     UUID NOT NULL REFERENCES deliveries(id) ON DELETE CASCADE,
    photo_url       VARCHAR(500) NOT NULL,
    photo_type      VARCHAR(20) NOT NULL DEFAULT 'proof',
    latitude        DECIMAL(10,7),
    longitude       DECIMAL(10,7),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_delivery_photos_delivery ON delivery_photos(delivery_id);
