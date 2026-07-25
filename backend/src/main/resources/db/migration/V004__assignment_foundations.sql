-- V004: Assignment Engine Foundations — GPS Events & Shopper Availability
--
-- C-001: Shopper GPS ping storage at tiered frequencies
-- B-005: Shopper availability (online/offline) status tracking
-- C-007: Geographic zone assignment support

-- GPS Events — high-frequency location data from shopper app
CREATE TABLE gps_events (
    id              UUID PRIMARY KEY,
    shopper_id      UUID NOT NULL,
    latitude        DECIMAL(10,7) NOT NULL,
    longitude       DECIMAL(10,7) NOT NULL,
    speed           DECIMAL(5,2),
    bearing         DECIMAL(5,2),
    accuracy        DECIMAL(5,2),
    recorded_at     TIMESTAMP NOT NULL,
    received_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_gps_shopper_time ON gps_events(shopper_id, recorded_at DESC);
CREATE INDEX idx_gps_recorded_time ON gps_events(recorded_at);

-- Shopper Availability — tracks online/offline state and current position
CREATE TABLE shopper_availabilities (
    id              UUID PRIMARY KEY,
    shopper_id      UUID NOT NULL UNIQUE,
    status          VARCHAR(20) NOT NULL DEFAULT 'offline',
    current_lat     DECIMAL(10,7),
    current_lng     DECIMAL(10,7),
    current_zone_id UUID REFERENCES zones(id),
    transport_type  VARCHAR(20),
    heartbeat_at    TIMESTAMP,
    online_at       TIMESTAMP,
    offline_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_avail_status_zone ON shopper_availabilities(status, current_zone_id);
CREATE INDEX idx_avail_shopper ON shopper_availabilities(shopper_id);

-- Shopper Work History — tracks assignment statistics for scoring (C-011)
CREATE TABLE shopper_work_history (
    id              UUID PRIMARY KEY,
    shopper_id      UUID NOT NULL,
    date            DATE NOT NULL,
    total_offers    INTEGER NOT NULL DEFAULT 0,
    accepted_offers INTEGER NOT NULL DEFAULT 0,
    completed_orders INTEGER NOT NULL DEFAULT 0,
    cancelled_orders INTEGER NOT NULL DEFAULT 0,
    total_earnings  INTEGER NOT NULL DEFAULT 0,
    online_minutes  INTEGER NOT NULL DEFAULT 0,
    UNIQUE(shopper_id, date)
);

CREATE INDEX idx_work_history_shopper ON shopper_work_history(shopper_id, date DESC);
