-- V010: Notification Service — Multi-channel messaging with templates
--
-- J-001: In-app notification storage for chat-like experience
-- J-003: Assignment notifications (push + in-app + SMS fallback)
-- J-004: Order status notifications to customer
-- J-006: Language-localised templates (Swahili / English)

-- Notification templates — message content per event type, bilingual
CREATE TABLE notification_templates (
    id                  UUID PRIMARY KEY,
    template_key        VARCHAR(100) NOT NULL UNIQUE,
    channel             VARCHAR(20) NOT NULL DEFAULT 'in_app',
    title_en            VARCHAR(200),
    title_sw            VARCHAR(200),
    body_en             TEXT NOT NULL,
    body_sw             TEXT NOT NULL,
    variables           VARCHAR(500),         -- comma-separated: shopper_name,order_number,eta
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Notifications — sent notification records (in-app inbox + audit trail)
CREATE TABLE notifications (
    id                  UUID PRIMARY KEY,
    recipient_id        UUID NOT NULL,
    recipient_type      VARCHAR(10) NOT NULL, -- 'customer', 'shopper', 'admin'
    channel             VARCHAR(20) NOT NULL, -- 'push', 'sms', 'in_app'
    template_key        VARCHAR(100),
    title               VARCHAR(200),
    body                TEXT NOT NULL,
    data                TEXT,                 -- JSON metadata for deep linking
    status              VARCHAR(20) NOT NULL DEFAULT 'pending',
    is_read             BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at             TIMESTAMP,
    read_at             TIMESTAMP,
    provider_reference  VARCHAR(100),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_recipient ON notifications(recipient_id, recipient_type, created_at DESC);
CREATE INDEX idx_notifications_status ON notifications(status, created_at);
-- Note: Partial index supported in PostgreSQL but not H2.
-- CREATE INDEX idx_notifications_unread ON notifications(recipient_id, is_read) WHERE is_read = FALSE;

-- Seed notification templates
INSERT INTO notification_templates (id, template_key, channel, title_en, title_sw, body_en, body_sw, variables)
VALUES
-- Order status notifications (J-004)
('a0000001-0000-0000-0000-000000000001', 'order.created', 'in_app',
 'Order Confirmed', 'Agizo Limehakikiwa',
 'Your order {order_number} has been created. Total: {total} TZS.',
 'Agizo lako {order_number} limeundwa. Jumla: {total} TZS.',
 'order_number,total'),

('a0000001-0000-0000-0000-000000000002', 'order.accepted', 'push',
 'Shopper Assigned', 'Mnuuzaji Amepewa',
 '{shopper_name} is heading to {market_name} to shop for your order {order_number}.',
 '{shopper_name} anaelekea {market_name} kununua bidhaa za agizo lako {order_number}.',
 'shopper_name,market_name,order_number'),

('a0000001-0000-0000-0000-000000000003', 'order.shopping', 'in_app',
 'Shopping Started', 'Ununuzi Umeanza',
 '{shopper_name} is now shopping for your items at {market_name}.',
 '{shopper_name} ananunua bidhaa zako {market_name}.',
 'shopper_name,market_name'),

('a0000001-0000-0000-0000-000000000004', 'order.delivery_started', 'push',
 'Out for Delivery', 'Anakuja Na Bidhaa',
 '{shopper_name} is on the way! ETA: {eta_minutes} minutes.',
 '{shopper_name} anakuja! ETA: {eta_minutes} dakika.',
 'shopper_name,eta_minutes'),

('a0000001-0000-0000-0000-000000000005', 'order.delivered', 'in_app',
 'Order Delivered', 'Agizo Limefika',
 'Your order {order_number} has been delivered. Rate your shopper!',
 'Agizo lako {order_number} limewasilishwa. Mpime mnuuzaji wako!',
 'order_number'),

-- Assignment notifications (J-003)
('a0000001-0000-0000-0000-000000000006', 'offer.received', 'push',
 'New Order Offer', 'Ofa Mpya',
 'New order available! {item_count} items, approximately {earnings} TZS earnings.',
 'Ofa mpya! Bidhaa {item_count}, takriban TZS {earnings} mapato.',
 'item_count,earnings'),

-- Delivery notifications
('a0000001-0000-0000-0000-000000000007', 'delivery.delay', 'push',
 'Delivery Delayed', 'Uwasilishaji Umechelewa',
 'Your delivery is delayed by {delay_minutes} minutes. Reason: {reason}.',
 'Uwasilishaji wako umechelewa kwa {delay_minutes} dakika. Sababu: {reason}.',
 'delay_minutes,reason'),

('a0000001-0000-0000-0000-000000000008', 'delivery.eta_update', 'push',
 'ETA Updated', 'ETA Imesasishwa',
 'Your delivery ETA has been updated to {eta_time}.',
 'ETA ya uwasilishaji wako imesasishwa hadi {eta_time}.',
 'eta_time');
