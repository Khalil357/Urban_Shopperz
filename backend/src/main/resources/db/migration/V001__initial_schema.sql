-- H2-compatible schema (uses PUBLIC/default schema)
-- For PostgreSQL, tables go into 'public' schema by default

CREATE TABLE customers (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone               VARCHAR(15) NOT NULL UNIQUE,
    name                VARCHAR(100) NOT NULL,
    language            VARCHAR(2) NOT NULL DEFAULT 'sw',
    notification_prefs  VARCHAR(255) NOT NULL DEFAULT '{"push": true, "sms": true, "in_app": true}',
    status              VARCHAR(20) NOT NULL DEFAULT 'active',
    trust_score         INTEGER NOT NULL DEFAULT 50,
    total_orders        INTEGER NOT NULL DEFAULT 0,
    last_active_at      TIMESTAMP,
    deactivated_at      TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_customers_phone ON customers(phone);
CREATE INDEX idx_customers_status ON customers(status);

CREATE TABLE zones (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                    VARCHAR(100) NOT NULL,
    city                    VARCHAR(100) NOT NULL DEFAULT 'Dar es Salaam',
    status                  VARCHAR(20) NOT NULL DEFAULT 'inactive',
    center_lat              NUMERIC(10,7),
    center_lng              NUMERIC(10,7),
    max_assignment_radius_km NUMERIC(5,2) NOT NULL DEFAULT 5.0,
    operating_hours_start   TIME NOT NULL DEFAULT '06:00',
    operating_hours_end     TIME NOT NULL DEFAULT '22:00',
    base_delivery_fee       INTEGER NOT NULL DEFAULT 1500,
    per_km_rate             INTEGER NOT NULL DEFAULT 500,
    supply_demand_threshold NUMERIC(5,2) NOT NULL DEFAULT 5.0,
    current_shopper_count   INTEGER NOT NULL DEFAULT 0,
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Seed default zones for Dar es Salaam
INSERT INTO zones (name, city, status, center_lat, center_lng, max_assignment_radius_km)
VALUES ('Mikocheni', 'Dar es Salaam', 'inactive', -6.7760, 39.2630, 6.0);

INSERT INTO zones (name, city, status, center_lat, center_lng, max_assignment_radius_km)
VALUES ('Kariakoo', 'Dar es Salaam', 'inactive', -6.8200, 39.2800, 6.0);

INSERT INTO zones (name, city, status, center_lat, center_lng, max_assignment_radius_km)
VALUES ('Mbezi', 'Dar es Salaam', 'inactive', -6.7380, 39.1080, 12.0);
