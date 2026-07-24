-- User Service Schema
CREATE SCHEMA IF NOT EXISTS user_service;

CREATE TABLE user_service.customers (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone               VARCHAR(15) NOT NULL UNIQUE,
    name                VARCHAR(100) NOT NULL,
    language            VARCHAR(2) NOT NULL DEFAULT 'sw',
    notification_prefs  JSONB NOT NULL DEFAULT '{"push": true, "sms": true, "in_app": true}',
    status              VARCHAR(20) NOT NULL DEFAULT 'active'
                        CHECK (status IN ('active', 'suspended', 'deactivated', 'dormant')),
    trust_score         INTEGER NOT NULL DEFAULT 50,
    total_orders        INTEGER NOT NULL DEFAULT 0,
    last_active_at      TIMESTAMPTZ,
    deactivated_at      TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT valid_phone CHECK (phone ~ '^255[0-9]{9}$'),
    CONSTRAINT valid_trust_score CHECK (trust_score BETWEEN 0 AND 100)
);

CREATE INDEX idx_customers_phone ON user_service.customers(phone);
CREATE INDEX idx_customers_status ON user_service.customers(status);

-- Order Service Schema
CREATE SCHEMA IF NOT EXISTS order_service;

CREATE TABLE order_service.zones (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                    VARCHAR(100) NOT NULL,
    city                    VARCHAR(100) NOT NULL DEFAULT 'Dar es Salaam',
    status                  VARCHAR(20) NOT NULL DEFAULT 'inactive'
                            CHECK (status IN ('active', 'limited', 'inactive')),
    center_lat              NUMERIC(10,7),
    center_lng              NUMERIC(10,7),
    max_assignment_radius_km NUMERIC(5,2) NOT NULL DEFAULT 5.0,
    operating_hours_start   TIME NOT NULL DEFAULT '06:00',
    operating_hours_end     TIME NOT NULL DEFAULT '22:00',
    base_delivery_fee       INTEGER NOT NULL DEFAULT 1500,
    per_km_rate             INTEGER NOT NULL DEFAULT 500,
    supply_demand_threshold NUMERIC(5,2) NOT NULL DEFAULT 5.0,
    current_shopper_count   INTEGER NOT NULL DEFAULT 0,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Payment Service Schema
CREATE SCHEMA IF NOT EXISTS payment_service;

-- Rating Service Schema
CREATE SCHEMA IF NOT EXISTS rating_service;

-- Dispute Service Schema
CREATE SCHEMA IF NOT EXISTS dispute_service;

-- Delivery Service Schema
CREATE SCHEMA IF NOT EXISTS delivery_service;

-- Notification Service Schema
CREATE SCHEMA IF NOT EXISTS notification_service;

-- Shared (cross-service) Schema
CREATE SCHEMA IF NOT EXISTS shared;

-- Seed default zone for Dar es Salaam
INSERT INTO order_service.zones (name, city, status, center_lat, center_lng, max_assignment_radius_km)
VALUES ('Mikocheni', 'Dar es Salaam', 'inactive', -6.7760, 39.2630, 6.0);

INSERT INTO order_service.zones (name, city, status, center_lat, center_lng, max_assignment_radius_km)
VALUES ('Kariakoo', 'Dar es Salaam', 'inactive', -6.8200, 39.2800, 6.0);

INSERT INTO order_service.zones (name, city, status, center_lat, center_lng, max_assignment_radius_km)
VALUES ('Mbezi', 'Dar es Salaam', 'inactive', -6.7380, 39.1080, 12.0);
