-- V007: Payment Service — Payment Processing & Shopper Wallets
--
-- F-003: Payment pre-authorisation and capture
-- F-004: Final payment calculation with variance approval
-- F-005: Tiered shopper payout (shopping fee + delivery fee)
-- F-006: 48-hour settlement period
-- F-010: Shopper wallet for earnings tracking
-- F-011: Shopper protection compensation

-- Payments — one per order, tracks the financial lifecycle
CREATE TABLE payments (
    id                  UUID PRIMARY KEY,
    order_id            UUID NOT NULL UNIQUE REFERENCES orders(id),
    customer_id         UUID NOT NULL,

    status              VARCHAR(20) NOT NULL DEFAULT 'pending',

    -- Amounts in TZS
    estimated_amount    INTEGER NOT NULL DEFAULT 0,
    captured_amount     INTEGER,
    service_fee         INTEGER,
    delivery_fee        INTEGER,
    item_cost           INTEGER,

    -- Shopper payout (F-005)
    shopper_payout      INTEGER,
    shopper_id          UUID,

    -- Provider tracking
    payment_method      VARCHAR(10) NOT NULL DEFAULT 'mpesa',
    provider            VARCHAR(20),
    provider_reference  VARCHAR(100),
    provider_status     VARCHAR(50),

    -- Timing
    authorized_at       TIMESTAMP,
    captured_at         TIMESTAMP,
    settled_at          TIMESTAMP,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_provider_ref ON payments(provider, provider_reference);

-- Payment transactions — audit trail of all financial events
CREATE TABLE payment_transactions (
    id                  UUID PRIMARY KEY,
    payment_id          UUID NOT NULL REFERENCES payments(id),
    transaction_type    VARCHAR(30) NOT NULL,
    amount              INTEGER NOT NULL,
    provider            VARCHAR(20),
    provider_reference  VARCHAR(100),
    provider_response   TEXT,
    status              VARCHAR(20) NOT NULL DEFAULT 'pending',
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payment_tx_payment ON payment_transactions(payment_id);
CREATE INDEX idx_payment_tx_provider ON payment_transactions(provider, provider_reference);

-- Shopper wallets — earnings tracking and withdrawal management (F-010)
CREATE TABLE shopper_wallets (
    id                  UUID PRIMARY KEY,
    shopper_id          UUID NOT NULL UNIQUE,
    available_balance   INTEGER NOT NULL DEFAULT 0,
    pending_balance     INTEGER NOT NULL DEFAULT 0,
    lifetime_earnings   INTEGER NOT NULL DEFAULT 0,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_wallets_shopper ON shopper_wallets(shopper_id);

-- Wallet transactions — earnings and withdrawal history
CREATE TABLE wallet_transactions (
    id                  UUID PRIMARY KEY,
    wallet_id           UUID NOT NULL REFERENCES shopper_wallets(id),
    transaction_type    VARCHAR(30) NOT NULL,
    amount              INTEGER NOT NULL,
    reference_type      VARCHAR(30),
    reference_id        UUID,
    description         VARCHAR(255),
    status              VARCHAR(20) NOT NULL DEFAULT 'completed',
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_wallet_tx_wallet ON wallet_transactions(wallet_id);
