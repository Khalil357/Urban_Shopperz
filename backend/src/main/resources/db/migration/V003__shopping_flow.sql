-- V003: Shopping Flow — Receipts, Markets, and Photo Evidence
--
-- D-008: Receipt upload supporting multiple formats (photo, handwritten, manual entry)
-- D-006: Optional photo per order item for quality verification
-- D-001: Market/source location reference

-- Markets — source locations where shoppers purchase items
CREATE TABLE markets (
    id              UUID PRIMARY KEY,
    name            VARCHAR(200) NOT NULL,
    zone_id         UUID NOT NULL REFERENCES zones(id),
    latitude        DECIMAL(10,7),
    longitude       DECIMAL(10,7),
    address_text    TEXT,
    status          VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_markets_zone ON markets(zone_id);

-- Receipts — proof of purchase, one or more per order
CREATE TABLE receipts (
    id              UUID PRIMARY KEY,
    order_id        UUID NOT NULL REFERENCES orders(id),
    receipt_type    VARCHAR(20) NOT NULL DEFAULT 'photo',
    total_amount    INTEGER,
    vendor_name     VARCHAR(200),
    notes           TEXT,
    is_verified     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_receipts_order ON receipts(order_id);

-- Receipt photos — supporting images per receipt
CREATE TABLE receipt_photos (
    id              UUID PRIMARY KEY,
    receipt_id      UUID NOT NULL REFERENCES receipts(id) ON DELETE CASCADE,
    photo_url       VARCHAR(500) NOT NULL,
    sort_order      INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_receipt_photos_receipt ON receipt_photos(receipt_id);

-- Seed data: Initial Dar es Salaam markets
INSERT INTO markets (id, name, zone_id, latitude, longitude, address_text, status)
VALUES
    ('a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'Mikocheni B Market',
     'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', -6.7765, 39.2620,
     'Mikocheni B, Dar es Salaam', 'active'),
    ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'Kariakoo Main Market',
     'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', -6.8195, 39.2790,
     'Kariakoo, Dar es Salaam', 'active'),
    ('c1eebc99-9c0b-4ef8-bb6d-6bb9bd380a03', 'Mbezi Beach Market',
     'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', -6.7385, 39.1085,
     'Mbezi Beach, Dar es Salaam', 'active');
