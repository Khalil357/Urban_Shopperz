-- V012: Admin Dashboard — Admin Users & Role-Based Access
--
-- L-001: Three admin tiers (Super Admin, Operations Admin, Support Agent)
-- L-004: Regulatory reporting foundation

-- Admin users with role-based access
CREATE TABLE admin_users (
    id                  UUID PRIMARY KEY,
    username            VARCHAR(50) NOT NULL UNIQUE,
    password_hash       VARCHAR(255) NOT NULL,
    role                VARCHAR(20) NOT NULL DEFAULT 'support_agent',
    status              VARCHAR(20) NOT NULL DEFAULT 'active',
    name                VARCHAR(100) NOT NULL,
    last_login_at       TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_admin_role CHECK (role IN ('super_admin', 'ops_admin', 'support_agent'))
);

-- Admin user seeded by AdminSeeder.java at startup
