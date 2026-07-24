# Urban Shopper Platform — Database Design

> **Document Type:** Technical Specification  
> **Status:** Complete  
> **Date:** 2026-07-24  
> **Version:** 1.0  
> **Cross-Reference:** Domain Model — `02-domain-model.md`, System Architecture — `07-system-architecture.md`, API Specification — `10-api-specification.md`  
> **Phase:** 11 of 12

---

## Table of Contents

1. [Database Principles](#1-database-principles)
2. [Schema Overview](#2-schema-overview)
3. [Table Definitions](#3-table-definitions)
4. [Index Strategy](#4-index-strategy)
5. [Partitioning Strategy](#5-partitioning-strategy)
6. [Key Queries & Optimisation](#6-key-queries--optimisation)

---

## 1. Database Principles

| Principle | Implementation |
|-----------|---------------|
| **Primary Database** | PostgreSQL 16+ with pgRouting extension for road distance calculations |
| **Time-Series Data** | TimescaleDB (PostgreSQL extension) for GPS location data |
| **Schema-per-Service** | Each microservice owns its schema. Tables in each schema are only modified by that service. |
| **UUID Primary Keys** | All primary keys use UUID v4. Enables sharding and distributed operations. |
| **Audit Trail** | Separate `audit_log` table — append-only, immutable. |
| **Soft Deletes** | Users and config data use soft deletes (`deleted_at`). Transactional data never deleted. |
| **JSONB for Flexibility** | Flexible fields (item options, notification preferences) use JSONB. |
| **Encryption at Rest** | PII columns encrypted using pgcrypto with AES-256. |

---

## 2. Schema Overview

```
┌────────────────────────────────────────────┐
│  user_service Schema                       │
│  ┌────────────────────────────────────┐    │
│  │ customers                          │    │
│  │ shoppers                           │    │
│  │ admins                             │    │
│  │ devices                            │    │
│  │ sessions                           │    │
│  │ shopper_documents                  │    │
│  │ shopper_verification               │    │
│  └────────────────────────────────────┘    │
├────────────────────────────────────────────┤
│  order_service Schema                      │
│  ┌────────────────────────────────────┐    │
│  │ orders                             │    │
│  │ order_items                        │    │
│  │ order_status_history               │    │
│  │ receipts                           │    │
│  │ receipt_photos                     │    │
│  │ markets                            │    │
│  │ zones                              │    │
│  └────────────────────────────────────┘    │
├────────────────────────────────────────────┤
│  payment_service Schema                    │
│  ┌────────────────────────────────────┐    │
│  │ payments                           │    │
│  │ payment_transactions               │    │
│  │ shopper_wallets                    │    │
│  │ wallet_transactions                │    │
│  │ customer_wallets (future)          │    │
│  └────────────────────────────────────┘    │
├────────────────────────────────────────────┤
│  delivery_service Schema                   │
│  ┌────────────────────────────────────┐    │
│  │ deliveries                         │    │
│  │ gps_events                         │    │  ← TimescaleDB hypertable
│  │ delivery_photos                    │    │
│  └────────────────────────────────────┘    │
├────────────────────────────────────────────┤
│  dispute_service Schema                    │
│  ┌────────────────────────────────────┐    │
│  │ disputes                           │    │
│  │ dispute_evidence                   │    │
│  │ dispute_decisions                  │    │
│  └────────────────────────────────────┘    │
├────────────────────────────────────────────┤
│  rating_service Schema                     │
│  ┌────────────────────────────────────┐    │
│  │ ratings                            │    │
│  │ rating_fraud_flags                 │    │
│  └────────────────────────────────────┘    │
├────────────────────────────────────────────┤
│  notification_service Schema               │
│  ┌────────────────────────────────────┐    │
│  │ notifications                      │    │
│  │ notification_templates             │    │
│  │ device_push_tokens                 │    │
│  └────────────────────────────────────┘    │
├────────────────────────────────────────────┤
│  Shared Schema                             │
│  ┌────────────────────────────────────┐    │
│  │ audit_log                          │    │  ← Append-only
│  │ promotions                         │    │
│  │ referrals                          │    │
│  │ promo_redemptions                  │    │
│  └────────────────────────────────────┘    │
└────────────────────────────────────────────┘
```

---

## 3. Table Definitions

### 3.1 user_service.customers

```sql
CREATE TABLE user_service.customers (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone               VARCHAR(15) NOT NULL UNIQUE,
    name                VARCHAR(100) NOT NULL,
    language            VARCHAR(2) NOT NULL DEFAULT 'sw' CHECK (language IN ('sw', 'en')),
    notification_prefs  JSONB NOT NULL DEFAULT '{"push": true, "sms": true, "in_app": true}',
    status              VARCHAR(20) NOT NULL DEFAULT 'active'
                        CHECK (status IN ('active', 'suspended', 'deactivated', 'dormant')),
    trust_score         INTEGER NOT NULL DEFAULT 50,
    total_orders        INTEGER NOT NULL DEFAULT 0,
    last_active_at      TIMESTAMPTZ,
    deactivated_at      TIMESTAMPTZ,
    dormant_at          TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Encrypted PII
    encrypted_id_data   BYTEA,  -- encrypted government ID if collected

    CONSTRAINT valid_phone CHECK (phone ~ '^255[0-9]{9}$'),
    CONSTRAINT valid_trust_score CHECK (trust_score BETWEEN 0 AND 100)
);

-- Indexes
CREATE INDEX idx_customers_phone ON user_service.customers(phone);
CREATE INDEX idx_customers_status ON user_service.customers(status);
CREATE INDEX idx_customers_last_active ON user_service.customers(last_active_at) WHERE status = 'active';
```

### 3.2 user_service.shoppers

```sql
CREATE TABLE user_service.shoppers (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone               VARCHAR(15) NOT NULL UNIQUE,
    name                VARCHAR(100) NOT NULL,
    status              VARCHAR(30) NOT NULL DEFAULT 'pending_verification'
                        CHECK (status IN (
                            'pending_verification', 'onboarding', 'active',
                            'suspended', 'low_priority', 'deactivated'
                        )),
    transport_type      VARCHAR(20) NOT NULL
                        CHECK (transport_type IN ('walking', 'bicycle', 'motorcycle', 'car', 'public_transport')),
    rating              NUMERIC(3,2) DEFAULT 0,
    rating_count        INTEGER NOT NULL DEFAULT 0,
    tier                VARCHAR(20) NOT NULL DEFAULT 'base'
                        CHECK (tier IN ('base', 'bronze', 'silver', 'gold', 'platinum')),
    acceptance_rate     NUMERIC(5,2) DEFAULT 100.00,
    completion_rate     NUMERIC(5,2) DEFAULT 100.00,
    trust_score         INTEGER NOT NULL DEFAULT 50,
    total_orders        INTEGER NOT NULL DEFAULT 0,
    lifetime_earnings   INTEGER NOT NULL DEFAULT 0,
    is_online           BOOLEAN NOT NULL DEFAULT FALSE,
    last_online_at      TIMESTAMPTZ,
    last_gps_at         TIMESTAMPTZ,
    current_zone_id     UUID,                    -- FK to zone
    emergency_contact_name    VARCHAR(100),
    emergency_contact_phone   VARCHAR(15),
    emergency_contact_rel     VARCHAR(30),
    accepted_code_of_conduct  BOOLEAN NOT NULL DEFAULT FALSE,
    onboarding_completed      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Suspension tracking
    suspension_count    INTEGER NOT NULL DEFAULT 0,
    suspension_until    TIMESTAMPTZ,
    last_warning_at     TIMESTAMPTZ
);

-- Indexes
CREATE INDEX idx_shoppers_phone ON user_service.shoppers(phone);
CREATE INDEX idx_shoppers_status ON user_service.shoppers(status);
CREATE INDEX idx_shoppers_online_zone ON user_service.shoppers(is_online, current_zone_id)
    WHERE is_online = TRUE;
CREATE INDEX idx_shoppers_tier ON user_service.shoppers(tier);
```

### 3.3 user_service.shopper_documents

```sql
CREATE TABLE user_service.shopper_documents (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shopper_id      UUID NOT NULL REFERENCES user_service.shoppers(id),
    doc_type        VARCHAR(30) NOT NULL
                    CHECK (doc_type IN ('national_id_front', 'national_id_back', 'selfie',
                           'driving_licence', 'vehicle_registration', 'insurance',
                           'police_clearance', 'passport', 'voter_id')),
    status          VARCHAR(20) NOT NULL DEFAULT 'pending'
                    CHECK (status IN ('pending', 'submitted', 'verified', 'rejected', 'expired')),
    file_url        TEXT NOT NULL,
    verified_by     UUID REFERENCES user_service.admins(id),
    verified_at     TIMESTAMPTZ,
    rejection_reason TEXT,
    expires_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_shopper_docs_shopper ON user_service.shopper_documents(shopper_id);
CREATE INDEX idx_shopper_docs_status ON user_service.shopper_documents(status, shopper_id);
```

### 3.4 order_service.zones

```sql
CREATE TABLE order_service.zones (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                    VARCHAR(100) NOT NULL,
    city                    VARCHAR(100) NOT NULL DEFAULT 'Dar es Salaam',
    status                  VARCHAR(20) NOT NULL DEFAULT 'inactive'
                            CHECK (status IN ('active', 'limited', 'inactive')),
    boundary_geometry       GEOMETRY(POLYGON, 4326),  -- PostGIS for boundary
    center_lat              NUMERIC(10,7),
    center_lng              NUMERIC(10,7),
    max_assignment_radius_km NUMERIC(5,2) NOT NULL DEFAULT 5.0,
    operating_hours_start   TIME NOT NULL DEFAULT '06:00',
    operating_hours_end     TIME NOT NULL DEFAULT '22:00',
    base_delivery_fee       INTEGER NOT NULL DEFAULT 1500,
    per_km_rate             INTEGER NOT NULL DEFAULT 500,
    surge_multiplier        NUMERIC(3,2) NOT NULL DEFAULT 1.00,
    supply_demand_threshold NUMERIC(5,2) NOT NULL DEFAULT 5.0,
    min_shoppers_active     INTEGER NOT NULL DEFAULT 5,
    current_shopper_count   INTEGER NOT NULL DEFAULT 0,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_zones_city ON order_service.zones(city);
```

### 3.5 order_service.markets

```sql
CREATE TABLE order_service.markets (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    zone_id         UUID NOT NULL REFERENCES order_service.zones(id),
    name            VARCHAR(200) NOT NULL,
    market_type     VARCHAR(30) NOT NULL DEFAULT 'open_market'
                    CHECK (market_type IN ('open_market', 'formal_retail', 'specialty')),
    latitude        NUMERIC(10,7) NOT NULL,
    longitude       NUMERIC(10,7) NOT NULL,
    location_geom   GEOMETRY(POINT, 4326),  -- PostGIS point
    address         TEXT,
    status          VARCHAR(20) NOT NULL DEFAULT 'active'
                    CHECK (status IN ('active', 'inactive', 'closed')),
    operating_hours_start TIME,
    operating_hours_end   TIME,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_markets_zone ON order_service.markets(zone_id);
CREATE INDEX idx_markets_location ON order_service.markets USING GIST(location_geom);
```

### 3.6 order_service.orders (Core Table)

```sql
CREATE TABLE order_service.orders (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number            VARCHAR(30) NOT NULL UNIQUE,
    customer_id             UUID NOT NULL REFERENCES user_service.customers(id),
    shopper_id              UUID REFERENCES user_service.shoppers(id),
    zone_id                 UUID NOT NULL REFERENCES order_service.zones(id),
    market_id               UUID NOT NULL REFERENCES order_service.markets(id),
    status                  VARCHAR(30) NOT NULL DEFAULT 'created'
                            CHECK (status IN (
                                'created', 'awaiting_payment_verification', 'queued_for_assignment',
                                'offered', 'accepted', 'travelling_to_market', 'shopping',
                                'shopping_complete', 'receipt_verified', 'in_delivery',
                                'delivered', 'completed', 'cancelled', 'archived'
                            )),
    shopping_preference     VARCHAR(20) NOT NULL DEFAULT 'balanced'
                            CHECK (shopping_preference IN ('cheapest', 'best_quality', 'balanced')),
    delivery_preference     VARCHAR(10) NOT NULL DEFAULT 'asap'
                            CHECK (delivery_preference IN ('asap', 'scheduled')),
    scheduled_window_start  TIMESTAMPTZ,
    scheduled_window_end    TIMESTAMPTZ,
    payment_method          VARCHAR(10) NOT NULL DEFAULT 'mpesa'
                            CHECK (payment_method IN ('mpesa', 'mixx', 'airtel', 'cod')),

    -- Delivery location
    delivery_lat            NUMERIC(10,7) NOT NULL,
    delivery_lng            NUMERIC(10,7) NOT NULL,
    delivery_address_text   TEXT,
    delivery_landmark       TEXT,

    -- Financial summary
    estimated_item_cost     INTEGER NOT NULL DEFAULT 0,
    estimated_service_fee   INTEGER NOT NULL DEFAULT 0,
    estimated_delivery_fee  INTEGER NOT NULL DEFAULT 0,
    estimated_total         INTEGER NOT NULL DEFAULT 0,
    actual_item_cost        INTEGER,
    actual_service_fee      INTEGER,
    actual_delivery_fee     INTEGER,
    actual_total            INTEGER,

    -- Cancellation
    cancellation_reason     TEXT,
    cancellation_fee        INTEGER DEFAULT 0,
    cancelled_by            VARCHAR(20),
    cancelled_at            TIMESTAMPTZ,

    -- Timing
    order_category          VARCHAR(30),    -- For inspection window
    item_count              INTEGER NOT NULL DEFAULT 0,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    archived_at             TIMESTAMPTZ,

    -- State machine tracking
    current_state_since     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Critical indexes for the assignment engine
CREATE INDEX idx_orders_status_zone ON order_service.orders(status, zone_id, created_at DESC)
    WHERE status IN ('queued_for_assignment', 'offered');
CREATE INDEX idx_orders_customer ON order_service.orders(customer_id, created_at DESC);
CREATE INDEX idx_orders_shopper_active ON order_service.orders(shopper_id)
    WHERE status NOT IN ('completed', 'cancelled', 'archived');
CREATE INDEX idx_orders_created ON order_service.orders(created_at DESC);
CREATE INDEX idx_orders_status ON order_service.orders(status);
```

### 3.7 order_service.order_items

```sql
CREATE TABLE order_service.order_items (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id                UUID NOT NULL REFERENCES order_service.orders(id) ON DELETE CASCADE,
    name                    VARCHAR(200) NOT NULL,
    quantity                INTEGER NOT NULL DEFAULT 1,
    unit                    VARCHAR(20),
    preferred_brand         VARCHAR(100),
    max_price               INTEGER,      -- customer's max acceptable price in TZS
    notes                   TEXT,
    status                  VARCHAR(20) NOT NULL DEFAULT 'requested'
                            CHECK (status IN ('requested', 'found', 'substituted', 'not_available')),
    substitution_preference VARCHAR(20)    -- per-item override
                            CHECK (substitution_preference IN ('best_match', 'contact_me', 'no_substitutions')),
    substitution_note       TEXT,
    substitution_customer_approval VARCHAR(20)
                            CHECK (substitution_customer_approval IN ('pending', 'approved', 'declined')),
    actual_price            INTEGER,
    has_photo               BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order              INTEGER NOT NULL DEFAULT 0,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_order_items_order ON order_service.order_items(order_id);
```

### 3.8 order_service.order_status_history

```sql
CREATE TABLE order_service.order_status_history (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id        UUID NOT NULL REFERENCES order_service.orders(id),
    from_status     VARCHAR(30) NOT NULL,
    to_status       VARCHAR(30) NOT NULL,
    trigger_event   VARCHAR(50) NOT NULL,
    actor_type      VARCHAR(20) NOT NULL,
    actor_id        UUID,                    -- customer, shopper, or admin ID
    reason          TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_status_history_order ON order_service.order_status_history(order_id, created_at);
```

### 3.9 payment_service.payments

```sql
CREATE TABLE payment_service.payments (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id            UUID NOT NULL UNIQUE REFERENCES order_service.orders(id),
    customer_id         UUID NOT NULL REFERENCES user_service.customers(id),
    shopper_id          UUID REFERENCES user_service.shoppers(id),
    payment_method      VARCHAR(10) NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'pending'
                        CHECK (status IN (
                            'pending', 'authorized', 'captured',
                            'settled', 'refunded', 'cancelled', 'completed'
                        )),
    pre_auth_amount     INTEGER NOT NULL,
    pre_auth_reference  VARCHAR(100),
    pre_auth_at         TIMESTAMPTZ,
    final_amount        INTEGER,
    capture_reference   VARCHAR(100),
    captured_at         TIMESTAMPTZ,
    shopper_payout      INTEGER,
    platform_revenue    INTEGER,
    settled_at          TIMESTAMPTZ,
    refunded_amount     INTEGER DEFAULT 0,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payments_order ON payment_service.payments(order_id);
CREATE INDEX idx_payments_customer ON payment_service.payments(customer_id);
CREATE INDEX idx_payments_status ON payment_service.payments(status);
```

### 3.10 payment_service.payment_transactions

```sql
CREATE TABLE payment_service.payment_transactions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_id          UUID NOT NULL REFERENCES payment_service.payments(id),
    transaction_type    VARCHAR(30) NOT NULL
                        CHECK (transaction_type IN (
                            'authorization', 'capture', 'refund',
                            'settlement', 'withdrawal', 'deposit',
                            'fee', 'adjustment'
                        )),
    amount              INTEGER NOT NULL,
    from_account        VARCHAR(30) NOT NULL,
    to_account          VARCHAR(30) NOT NULL,
    external_reference  VARCHAR(100),
    status              VARCHAR(20) NOT NULL DEFAULT 'pending'
                        CHECK (status IN ('pending', 'completed', 'failed')),
    failure_reason      TEXT,
    admin_id            UUID REFERENCES user_service.admins(id),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payment_txns_payment ON payment_service.payment_transactions(payment_id);
CREATE INDEX idx_payment_txns_created ON payment_service.payment_transactions(created_at);
```

### 3.11 payment_service.shopper_wallets

```sql
CREATE TABLE payment_service.shopper_wallets (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shopper_id          UUID NOT NULL UNIQUE REFERENCES user_service.shoppers(id),
    available_balance   INTEGER NOT NULL DEFAULT 0,
    pending_balance     INTEGER NOT NULL DEFAULT 0,
    lifetime_earnings   INTEGER NOT NULL DEFAULT 0,
    lifetime_withdrawn  INTEGER NOT NULL DEFAULT 0,
    status              VARCHAR(20) NOT NULL DEFAULT 'active'
                        CHECK (status IN ('active', 'frozen', 'closed')),
    last_withdrawal_at  TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### 3.12 delivery_service.gps_events (TimescaleDB Hypertable)

```sql
CREATE TABLE delivery_service.gps_events (
    id              UUID NOT NULL DEFAULT gen_random_uuid(),
    shopper_id      UUID NOT NULL REFERENCES user_service.shoppers(id),
    order_id        UUID REFERENCES order_service.orders(id),
    latitude        NUMERIC(10,7) NOT NULL,
    longitude       NUMERIC(10,7) NOT NULL,
    accuracy_m      NUMERIC(5,1),
    speed_kmh       NUMERIC(5,1),
    bearing         NUMERIC(5,1),
    shopper_state   VARCHAR(20)       -- state at time of ping
                    CHECK (shopper_state IN (
                        'online', 'offered', 'travelling', 'shopping', 'delivering'
                    )),
    recorded_at     TIMESTAMPTZ NOT NULL
);

-- Convert to hypertable (TimescaleDB)
SELECT create_hypertable('delivery_service.gps_events', 'recorded_at',
    chunk_time_interval => INTERVAL '1 day');

-- Indexes
CREATE INDEX idx_gps_shopper_time ON delivery_service.gps_events(shopper_id, recorded_at DESC);
CREATE INDEX idx_gps_order ON delivery_service.gps_events(order_id, recorded_at);
CREATE INDEX idx_gps_cleanup ON delivery_service.gps_events(recorded_at)
    WHERE recorded_at < NOW() - INTERVAL '30 days';  -- For purge queries
```

### 3.13 dispute_service.disputes

```sql
CREATE TABLE dispute_service.disputes (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id            UUID NOT NULL REFERENCES order_service.orders(id),
    reported_by         VARCHAR(20) NOT NULL CHECK (reported_by IN ('customer', 'shopper', 'system')),
    dispute_type        VARCHAR(30) NOT NULL
                        CHECK (dispute_type IN (
                            'item_discrepancy', 'cancellation', 'refund',
                            'shopper_behaviour', 'payment_failure', 'other'
                        )),
    severity            VARCHAR(10) NOT NULL CHECK (severity IN ('minor', 'moderate', 'major')),
    status              VARCHAR(30) NOT NULL DEFAULT 'reported'
                        CHECK (status IN (
                            'reported', 'under_review', 'evidence_collection',
                            'decision', 'resolved', 'closed'
                        )),
    resolution_path     VARCHAR(20) CHECK (resolution_path IN ('automated', 'manual', 'escalated')),
    assigned_to         UUID REFERENCES user_service.admins(id),
    description         TEXT NOT NULL,
    decision            VARCHAR(20) CHECK (decision IN ('approved', 'rejected', 'partial_refund')),
    decision_rationale  TEXT,
    refund_amount       INTEGER DEFAULT 0,
    goodwill_amount     INTEGER DEFAULT 0,
    resolved_at         TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_disputes_order ON dispute_service.disputes(order_id);
CREATE INDEX idx_disputes_status ON dispute_service.disputes(status, created_at)
    WHERE status NOT IN ('resolved', 'closed');
CREATE INDEX idx_disputes_assigned ON dispute_service.disputes(assigned_to)
    WHERE assigned_to IS NOT NULL AND status NOT IN ('resolved', 'closed');
```

### 3.14 rating_service.ratings

```sql
CREATE TABLE rating_service.ratings (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id        UUID NOT NULL REFERENCES order_service.orders(id),
    rater_id        UUID NOT NULL,        -- customer or shopper
    rater_type      VARCHAR(10) NOT NULL CHECK (rater_type IN ('customer', 'shopper')),
    ratee_id        UUID NOT NULL,        -- shopper or customer
    ratee_type      VARCHAR(10) NOT NULL CHECK (ratee_type IN ('customer', 'shopper')),
    score           INTEGER NOT NULL CHECK (score BETWEEN 1 AND 5),
    feedback        TEXT,
    criteria        JSONB,                -- criteria breakdown scores
    status          VARCHAR(20) NOT NULL DEFAULT 'pending'
                    CHECK (status IN ('pending', 'submitted', 'revealed', 'excluded')),
    exclusion_reason VARCHAR(50),
    submitted_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    revealed_at     TIMESTAMPTZ
);

CREATE INDEX idx_ratings_order ON rating_service.ratings(order_id);
CREATE INDEX idx_ratings_ratee ON rating_service.ratings(ratee_id, ratee_type, submitted_at DESC);
CREATE INDEX idx_ratings_rater ON rating_service.ratings(rater_id, rater_type, submitted_at DESC);
```

### 3.15 shared.audit_log (Append-Only)

```sql
CREATE TABLE shared.audit_log (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_type      VARCHAR(30) NOT NULL
                    CHECK (event_type IN (
                        'financial', 'admin_action', 'status_change', 'security', 'system'
                    )),
    actor_type      VARCHAR(20) NOT NULL,
    actor_id        UUID,
    action          VARCHAR(100) NOT NULL,
    resource_type   VARCHAR(30) NOT NULL,
    resource_id     UUID NOT NULL,
    previous_value  JSONB,
    new_value       JSONB,
    ip_address      INET,
    user_agent      TEXT,
    metadata        JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
) WITH (FILLFACTOR = 90);

-- IMPORTANT: No UPDATE or DELETE permissions granted on this table
-- Only INSERT and SELECT

CREATE INDEX idx_audit_resource ON shared.audit_log(resource_type, resource_id, created_at);
CREATE INDEX idx_audit_event ON shared.audit_log(event_type, created_at DESC);
CREATE INDEX idx_audit_actor ON shared.audit_log(actor_id, created_at DESC);
CREATE INDEX idx_audit_created ON shared.audit_log(created_at);
```

---

## 4. Index Strategy

### 4.1 Critical Path Indexes

| Query Pattern | Table | Index | Rationale |
|---------------|-------|-------|-----------|
| Find active shoppers in zone | `shoppers` | `(is_online, current_zone_id) WHERE is_online = TRUE` | Assignment engine — runs every few seconds |
| Find pending orders for assignment | `orders` | `(status, zone_id, created_at) WHERE status IN (...) ` | Assignment engine queue |
| Get shopper's current active order | `orders` | `(shopper_id) WHERE status NOT IN (...) ` | Shopper app — load current order |
| Get customer's order history | `orders` | `(customer_id, created_at DESC)` | Customer app — order list |
| Find nearest shoppers to market | `shoppers` | GIST index on last GPS point | Distance calculation |
| GPS data cleanup | `gps_events` | `(recorded_at) WHERE recorded_at < ...` | 30-day purge |
| Dispute queue | `disputes` | `(status, created_at) WHERE status NOT IN ...` | Admin dashboard |

### 4.2 Full-Text Search

```sql
-- Order search for admin
CREATE INDEX idx_orders_search ON order_service.orders USING GIN (
    to_tsvector('swahili', coalesce(order_number, ''))
);
```

---

## 5. Partitioning Strategy

### 5.1 GPS Events (TimescaleDB Hypertable)

Hypertable chunk interval: **1 day**. Retention policy: drop chunks older than **30 days**.

```sql
SELECT add_retention_policy('delivery_service.gps_events', INTERVAL '30 days');
```

### 5.2 Order Archival (Table Partitioning)

Orders are archived from the active `orders` table to an archive schema after 90 days.

```sql
-- Archive table structure (same columns as orders)
CREATE TABLE order_service.orders_archived (LIKE order_service.orders INCLUDING ALL);

-- Partitioned by archive year-month
CREATE TABLE order_service.orders_archived_2026_07
    PARTITION OF order_service.orders_archived
    FOR VALUES FROM ('2026-07-01') TO ('2026-08-01');
```

### 5.3 Audit Log (Partitioning)

```sql
-- Partition audit log by month
CREATE TABLE shared.audit_log_2026_07
    PARTITION OF shared.audit_log
    FOR VALUES FROM ('2026-07-01') TO ('2026-08-01');
```

---

## 6. Key Queries & Optimisation

### 6.1 Assignment Engine: Find Nearest Active Shoppers

```sql
-- Step 1: Haversine filter for nearest 20 shoppers
-- (executed in application code against Redis cache of active shopper positions)
-- Step 2: Road distance calculation via pgRouting
-- (or external Maps API)

-- PostgreSQL with PostGIS Haversine
SELECT
    s.id,
    s.current_zone_id,
    ST_Distance(
        sg.location::geography,
        ST_SetSRID(ST_MakePoint(:market_lng, :market_lat), 4326)::geography
    ) / 1000 AS distance_km
FROM user_service.shoppers s
JOIN (
    SELECT DISTINCT ON (shopper_id) shopper_id, location
    FROM delivery_service.current_gps_positions
) sg ON sg.shopper_id = s.id
WHERE s.is_online = TRUE
    AND s.status = 'active'
    AND s.current_zone_id = :zone_id
ORDER BY distance_km ASC
LIMIT 20;
```

### 6.2 Calculate Shopper Acceptance Rate

```sql
-- Rolling 30-day acceptance rate
SELECT
    shopper_id,
    ROUND(
        COUNT(*) FILTER (WHERE outcome = 'accepted') * 100.0 /
        NULLIF(COUNT(*), 0), 2
    ) AS acceptance_rate
FROM assignment_service.offer_log
WHERE shopper_id = :shopper_id
    AND offered_at >= NOW() - INTERVAL '30 days'
    AND excluded_from_rate = FALSE
GROUP BY shopper_id;
```

### 6.3 Get Active Orders for Zone Dispatch

```sql
-- Orders queued for assignment, oldest first
SELECT o.id, o.zone_id, o.market_id, o.item_count, o.estimated_total,
       o.created_at, m.latitude AS market_lat, m.longitude AS market_lng
FROM order_service.orders o
JOIN order_service.markets m ON m.id = o.market_id
WHERE o.status = 'queued_for_assignment'
    AND o.zone_id = :zone_id
ORDER BY o.created_at ASC
LIMIT 10;
```

### 6.4 Customer Trust Score Calculation

```sql
-- Simplified Trust Score query (scores 0-100)
SELECT
    c.id,
    ROUND(
        (COALESCE(payment_score, 0) * 0.35) +
        (COALESCE(volume_score, 0) * 0.25) +
        (COALESCE(cancellation_score, 0) * 0.15) +
        (COALESCE(age_score, 0) * 0.10) +
        (COALESCE(shopper_rating_score, 0) * 0.10) +
        (COALESCE(dispute_score, 0) * 0.05), 0
    ) AS trust_score
FROM user_service.customers c
LEFT JOIN LATERAL (
    -- Payment history score
    SELECT LEAST(
        (COUNT(*) FILTER (WHERE pt.status = 'completed' AND pt.transaction_type = 'capture') * 10), 100
    ) AS payment_score
    FROM payment_service.payment_transactions pt
    JOIN payment_service.payments p ON p.id = pt.payment_id
    WHERE p.customer_id = c.id
) payment ON TRUE
-- ... additional subqueries for other factors
WHERE c.id = :customer_id;
```

---

*This document is Phase 11 of the Urban Shopper Platform specification. It defines the complete database schema for all services and is intended for use by backend developers and database administrators.*
