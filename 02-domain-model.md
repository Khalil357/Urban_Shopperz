# Urban Shopper Platform — Domain Model

> **Document Type:** Business Analysis  
> **Status:** Complete  
> **Date:** 2026-07-24  
> **Version:** 1.0  
> **Cross-Reference:** Business Vision — `01-business-vision-and-strategy.md`, Business Rules — `03-business-rules.md`  
> **Phase:** 2 of 12

---

## Table of Contents

1. [Domain Model Overview](#1-domain-model-overview)
2. [Entity Catalogue](#2-entity-catalogue)
   - [Customer](#21-customer)
   - [Shopper](#22-shopper)
   - [Administrator](#23-administrator)
   - [Order](#24-order)
   - [Order Item](#25-order-item)
   - [Market](#26-market)
   - [Zone](#27-zone)
   - [Payment](#28-payment)
   - [Payment Transaction](#29-payment-transaction)
   - [Shopper Wallet](#210-shopper-wallet)
   - [Customer Wallet (Future)](#211-customer-wallet-future)
   - [Delivery](#212-delivery)
   - [Dispute](#213-dispute)
   - [Rating](#214-rating)
   - [Notification](#215-notification)
   - [Promotion](#216-promotion)
   - [Referral](#217-referral)
   - [Audit Log](#218-audit-log)
   - [Receipt](#219-receipt)
   - [GPS Location](#220-gps-location)
   - [Device](#221-device)
   - [Session](#222-session)
3. [Entity Relationship Diagram](#3-entity-relationship-diagram)
4. [Key Relationship Rules](#4-key-relationship-rules)

---

## 1. Domain Model Overview

The Urban Shopper domain consists of **22 core entities** organised into six logical groups:

| Group | Entities | Description |
|-------|----------|-------------|
| **Actors** | Customer, Shopper, Administrator | The people and roles that interact with the platform |
| **Commerce** | Order, Order Item, Market, Zone | The core transaction objects |
| **Financial** | Payment, Payment Transaction, Shopper Wallet, Customer Wallet | Money movement |
| **Fulfillment** | Delivery, Receipt, GPS Location | The physical delivery of goods |
| **Quality** | Rating, Dispute | Trust and accountability |
| **Infrastructure** | Notification, Promotion, Referral, Audit Log, Device, Session | Supporting systems |

Every business rule from the Business Rules Catalogue (`03-business-rules.md`) maps to one or more of these entities. The domain model is the shared vocabulary that the SRS, API specification, and database design will reference.

---

## 2. Entity Catalogue

---

### 2.1 Customer

| Attribute | Detail |
|-----------|--------|
| **Purpose** | A person who uses the platform to request shopping and delivery services |
| **Responsibilities** | Register and maintain their account. Create and manage orders. Pay for services. Rate shoppers. Communicate with assigned shoppers. Report issues. |
| **Lifecycle** | `Registered` → `Phone Verified` → `Active` → `Suspended` (platform) / `Deactivated` (self) → `Dormant` (12mo inactivity) → `Archived` (3yr after deactivation) |
| **Ownership** | Platform — customer data is regulated by privacy and data retention policies |

**Key Attributes:**
- Customer ID (UUID)
- Phone number (unique, verified via OTP)
- Full name
- Language preference (Swahili / English)
- Notification preferences (push / SMS / in-app)
- Account status (Active / Suspended / Deactivated / Dormant)
- Registration timestamp
- Last active timestamp
- Customer Trust Score (computed: F-012)

**Business Rules:** A-001 through A-010, F-012, H-002, I-001, I-002, I-008

**Relationships:**
- A Customer **creates** many Orders (1:N)
- A Customer **receives** many Deliveries (1:N)
- A Customer **submits** many Ratings (1:N)
- A Customer **initiates** many Disputes (1:N)
- A Customer **owns** one Device (1:1 active)
- A Customer **has** many Sessions (1:N)
- A Customer **may** have one Customer Wallet (1:0..1, future)

---

### 2.2 Shopper

| Attribute | Detail |
|-----------|--------|
| **Purpose** | An independent contractor who accepts orders, purchases items from markets, and delivers them to customers |
| **Responsibilities** | Register and complete verification. Accept/reject order offers. Shop items according to customer preferences. Upload receipts. Deliver items. Communicate with customers. Rate customers. Maintain documents. Follow platform policies. |
| **Lifecycle** | `Registered` → `Pending Verification` (ID + documents + background) → `Approved` → `Onboarding` → `Active` (Online/Offline) → `Suspended` → `Deactivated`. Online/Offline toggles within Active status. |
| **Ownership** | Platform — shopper is an independent contractor; platform manages verification, ratings, and compliance |

**Key Attributes:**
- Shopper ID (UUID)
- Phone number (unique, OTP verified)
- Full name
- Government ID reference (NIDA or other)
- Police Clearance Certificate status (Pending / Submitted / Verified)
- Transport type (Walking / Bicycle / Motorcycle / Car)
- Transport documents (registration, licence, insurance — per type)
- Mobile money account for payouts
- Emergency contact
- Account status
- Performance tier (Base / Bronze / Silver / Gold / Platinum)
- Acceptance rate (rolling 30-day)
- Completion rate (rolling 30-day)
- Average rating (recency-weighted)
- Trust Score (computed)
- Available balance (wallet)
- Registration timestamp
- GPS ping data (per state)

**Business Rules:** B-001 through B-014, C-001 through C-011, H-001 through H-008, I-003, I-009

**Relationships:**
- A Shopper **fulfills** many Orders (1:N)
- A Shopper **receives** many Payments (1:N)
- A Shopper **submits** many Ratings (1:N)
- A Shopper **is involved in** many Disputes (1:N)
- A Shopper **owns** one Shopper Wallet (1:1)
- A Shopper **has** many GPS Locations (1:N)
- A Shopper **owns** one Device (1:1 active)

---

### 2.3 Administrator

| Attribute | Detail |
|-----------|--------|
| **Purpose** | A platform staff member who manages operations, resolves disputes, monitors fraud, and oversees platform health |
| **Responsibilities** | Vary by role: vet shoppers, resolve disputes, investigate fraud, handle emergencies, monitor metrics, generate reports |
| **Lifecycle** | `Created` → `Active` → `Deactivated` (managed by Super Admin) |
| **Ownership** | Platform (employer) |

**Key Attributes:**
- Admin ID (UUID)
- Email (unique)
- Full name
- Role (Super Admin / Operations Admin / Support Agent)
- Account status
- Last login timestamp
- MFA status

**Business Rules:** L-001

**Relationships:**
- An Administrator **reviews** many Shopper applications (1:N)
- An Administrator **resolves** many Disputes (1:N)
- An Administrator **investigates** many Fraud Alerts (1:N)
- An Administrator **performs** many Audit Log entries (1:N)

---

### 2.4 Order

| Attribute | Detail |
|-----------|--------|
| **Purpose** | The core transaction entity — a customer's request for items to be shopped and delivered |
| **Responsibilities** | Track the complete lifecycle of a shopping request from creation through fulfillment and archival. Hold all information needed by the shopper to fulfill the request. Record financial details for billing. |
| **Lifecycle** | `Created` → `Awaiting Payment Verification` → `Queued for Assignment` → `Offered` → `Accepted` → `Travelling to Market` → `Shopping` → `Shopping Complete` → `Receipt Verified` → `In Delivery` → `Delivered` → `Completed` → `Archived`. Cancellation possible from multiple states with different fee rules. |
| **Ownership** | Customer — but platform governs the lifecycle, cancellation rules, and archival |

**Key Attributes:**
- Order ID (UUID)
- Customer ID (FK)
- Shopper ID (FK, nullable until assigned)
- Zone ID (FK)
- Market ID (FK)
- Status (state machine: D-004)
- Shopping preference (Cheapest Available / Best Quality / Balanced)
- Estimated item cost
- Actual item cost (after receipt)
- Platform service fee (tiered per F-001)
- Delivery fee (zone-configurable per F-002)
- Total estimated cost
- Total actual cost
- Substitution preference (per-order default)
- Delivery time preference (ASAP / Scheduled)
- Scheduled delivery window (if applicable)
- Cancellation reason (if cancelled)
- Cancellation fee (if applicable)
- Order category (for inspection window: E-007)
- Created timestamp
- State transition timestamps (per D-004)
- Archived timestamp

**Business Rules:** D-001 through D-014, C-001 through C-011, G-001 through G-011, E-001 through E-009, F-001 through F-012

**Relationships:**
- An Order **belongs to** one Customer (N:1)
- An Order **is fulfilled by** one Shopper (N:1) (nullable)
- An Order **contains** many Order Items (1:N)
- An Order **targets** one Market (N:1)
- An Order **belongs to** one Zone (N:1)
- An Order **has** one Payment (1:1)
- An Order **has** one Delivery (1:1)
- An Order **may have** many Disputes (1:N)
- An Order **may have** many Ratings (1:N)
- An Order **generates** one Receipt (shopper-uploaded) (1:1)

---

### 2.5 Order Item

| Attribute | Detail |
|-----------|--------|
| **Purpose** | A single line item within an order — represents one product the customer wants the shopper to purchase |
| **Responsibilities** | Record the customer's request, the shopper's action, and the final outcome for each individual item |
| **Lifecycle** | `Requested` → `Found` / `Substituted` / `Not Available`. Substituted items may have an approval sub-flow. |
| **Ownership** | Order (cascade delete) |

**Key Attributes:**
- Order Item ID (UUID)
- Order ID (FK)
- Item name (customer-entered)
- Quantity
- Unit (kg, pieces, litres, etc.) — optional
- Preferred brand — optional
- Maximum acceptable price — optional
- Customer notes (e.g., "green bananas, not too ripe")
- Shopping preference override (optional, per-item)
- Status (Found / Substituted / Not Available)
- Substitution shopper note — if substituted
- Substitution customer approval status (Pending / Approved / Declined)
- Actual price paid (from receipt or manual entry)
- Optional photo (shopper-uploaded for quality issues)
- Order category (for inspection window)

**Business Rules:** D-002, D-006, D-007

**Relationships:**
- An Order Item **belongs to** one Order (N:1)
- An Order Item **may have** one optional photo (1:0..1)
- An Order Item **may have** one Dispute (if item-specific) (1:0..1)

---

### 2.6 Market

| Attribute | Detail |
|-----------|--------|
| **Purpose** | A physical market or store where shoppers purchase items. May be a formal retailer or an informal market. |
| **Responsibilities** | Provide a known location for shoppers to source items. Serve as the origin point for delivery distance calculation. |
| **Lifecycle** | `Registered` → `Active` → `Inactive` (temporary) / `Closed` (permanent). Managed by administrators. |
| **Ownership** | Platform — in V1, markets are platform-registered; V2 allows vendor self-registration |

**Key Attributes:**
- Market ID (UUID)
- Name
- Zone ID (FK)
- GPS coordinates (lat, long)
- Address / landmark description
- Operating hours (per zone default + market-specific override)
- Market type (Formal Retail / Open Market / Specialty)
- Status (Active / Inactive / Closed)
- Contact information (optional)
- Typical item categories available (for future matching)

**Business Rules:** C-007, D-001, D-005, K-003

**Relationships:**
- A Market **belongs to** one Zone (N:1)
- A Market **is the source for** many Orders (1:N)
- A Market **may have** alternative markets nearby (self-referential, for K-003 market closure handling)

---

### 2.7 Zone

| Attribute | Detail |
|-----------|--------|
| **Purpose** | A geographic area (neighbourhood-level) that organises operations — pricing, assignment, hours, and shopper allocation |
| **Responsibilities** | Define the operating boundary for assignment, pricing, and scheduling. Enable zone-level management and phased rollout. |
| **Lifecycle** | `Defined` → `Active` → `Limited` → `Inactive`. Zones can be activated/deactivated independently. |
| **Ownership** | Platform |

**Key Attributes:**
- Zone ID (UUID)
- Name (e.g., "Kariakoo", "Mikocheni")
- City
- Center coordinates (lat, long)
- Boundary polygon (GPS coordinates defining the zone)
- Status (Active / Limited / Inactive)
- Operating hours (default 06:00-22:00)
- Maximum Assignment Radius (road distance, configurable)
- Base delivery fee (configurable portion of F-002)
- Per-km delivery rate (configurable portion of F-002)
- Service fee tier overrides (default uses standard F-001 tiers)
- Surge rules (late-night multiplier, demand-based)
- Minimum shopper count for activation
- Current shopper count (live)
- Supply/Demand ratio (live — C-006)

**Business Rules:** C-005, C-006, C-007, B-013, E-001, F-002

**Relationships:**
- A Zone **contains** many Markets (1:N)
- A Zone **is served by** many Shoppers (1:N)
- A Zone **has** many Orders sourced from its markets (1:N)

---

### 2.8 Payment

| Attribute | Detail |
|-----------|--------|
| **Purpose** | The financial record of a single order — tracks what was charged, what was paid, and to whom |
| **Responsibilities** | Record the financial obligations and settlements for each order. Ensure every payment is traceable and auditable. |
| **Lifecycle** | `Pending` → `Authorized` (pre-auth hold) → `Captured` (final amount confirmed) → `Settled` (funds distributed to shopper and platform) → `Refunded` (partial or full) → `Cancelled` (hold released, no charge). |
| **Ownership** | Platform (financial record) |

**Key Attributes:**
- Payment ID (UUID)
- Order ID (FK) (unique — 1:1 with Order)
- Customer ID (FK)
- Shopper ID (FK)
- Payment method (M-Pesa / Mixx / Airtel Money / COD)
- Pre-auth hold amount
- Pre-auth hold reference
- Pre-auth hold timestamp
- Final item cost (from receipt)
- Platform service fee (computed per F-001)
- Delivery fee (computed per F-002)
- Shopping fee (computed per F-005)
- Total customer charge
- Shopper payout amount
- Platform revenue (service fee - costs)
- Status (Pending / Authorized / Captured / Settled / Refunded / Cancelled)
- Settlement timestamp (when shopper was paid)

**Business Rules:** F-001 through F-012, M-010

**Relationships:**
- A Payment **belongs to** one Order (1:1)
- A Payment **has** many Payment Transactions (1:N)
- A Payment **pays** one Shopper (N:1)
- A Payment **is paid by** one Customer (N:1)

---

### 2.9 Payment Transaction

| Attribute | Detail |
|-----------|--------|
| **Purpose** | An individual financial event within a payment lifecycle — deposit, hold, capture, refund, payout, withdrawal |
| **Responsibilities** | Record every atomic financial event with complete traceability. Enable audit, reconciliation, and dispute investigation. |
| **Lifecycle** | Created (append-only — never modified or deleted) |
| **Ownership** | Platform (immutable financial record) |

**Key Attributes:**
- Transaction ID (UUID)
- Payment ID (FK)
- Transaction type (Deposit / Hold / Capture / Refund / Payout / Withdrawal / Fee / Adjustment)
- Amount (TZS)
- Currency (TZS)
- From account (Customer Wallet / Shopper Wallet / Platform / External)
- To account (Customer Wallet / Shopper Wallet / Platform / External)
- External reference (M-Pesa transaction ID, if applicable)
- Status (Pending / Completed / Failed)
- Failure reason (if failed)
- Timestamp
- Admin ID (FK, nullable — if manual adjustment)

**Business Rules:** L-002 (immutable audit logging)

**Relationships:**
- A Payment Transaction **belongs to** one Payment (N:1)
- A Payment Transaction **may be initiated by** one Administrator (N:0..1)

---

### 2.10 Shopper Wallet

| Attribute | Detail |
|-----------|--------|
| **Purpose** | An in-platform balance tracking mechanism for shopper earnings |
| **Responsibilities** | Track pending earnings, settled balance, and withdrawal history. Enable 48-hour settlement. Provide shopper visibility into earnings. |
| **Lifecycle** | `Created` (on shopper approval) → `Active` → `Closed` (on shopper deactivation, balance zeroed) |
| **Ownership** | Shopper (balance) / Platform (system) |

**Key Attributes:**
- Wallet ID (UUID)
- Shopper ID (FK) (1:1)
- Pending balance (earnings from orders not yet settled)
- Available balance (settled earnings ready for withdrawal)
- Lifetime earnings (cumulative)
- Lifetime withdrawals (cumulative)
- Current balance (available + pending)
- Status (Active / Frozen / Closed)
- Withdrawal minimum (2,000 TZS)
- Withdrawal maximum per day (200,000 TZS, tier-adjustable)
- Last withdrawal timestamp

**Business Rules:** F-010, F-006

**Relationships:**
- A Shopper Wallet **belongs to** one Shopper (1:1)
- A Shopper Wallet **has** many Payment Transactions (withdrawals) (1:N)

---

### 2.11 Customer Wallet (Future)

| Attribute | Detail |
|-----------|--------|
| **Purpose** | An in-platform balance for customers to deposit funds, receive refunds, and manage promotional credits |
| **Responsibilities** | Enable pre-funding of orders, simplify refunds, hold promotional credits, provide transaction history |
| **Lifecycle** | Deferred to V1. When implemented: `Created` → `Active` → `Closed` |
| **Ownership** | Customer (balance) / Platform (system) |

**Key Attributes:**
- Wallet ID (UUID)
- Customer ID (FK) (1:1)
- Balance
- Deposit history
- Refund history
- Promotional credit balance
- Max balance (500,000 TZS)
- Status

**Business Rules:** F-009

**Relationships:**
- A Customer Wallet **belongs to** one Customer (1:1) [future]

---

### 2.12 Delivery

| Attribute | Detail |
|-----------|--------|
| **Purpose** | The physical act of transporting purchased items from the market to the customer |
| **Responsibilities** | Track the delivery journey from shopping completion through handover. Record proof of delivery. Monitor ETA and delays. |
| **Lifecycle** | `Pending` (shopping not yet complete) → `In Transit` (shopper traveling) → `Arrived` → `Delivered` → `Confirmed` (customer confirms or 15-min auto) |
| **Ownership** | Order (1:1) |

**Key Attributes:**
- Delivery ID (UUID)
- Order ID (FK) (1:1)
- Shopper ID (FK)
- Customer ID (FK)
- Source market (FK)
- Delivery address (from Order)
- Delivery GPS coordinates (pin-drop)
- Shopper route (GPS trace during delivery)
- Status
- Predicted ETA (computed per E-003)
- Actual arrival timestamp
- Proof of delivery photo reference
- Delivery recipient type (Customer / Authorized Recipient / Safe Drop)
- Authorized recipient name (optional)
- Authorized recipient relationship (optional)
- Delay reason (if applicable)
- Delay flag (support notified)
- Redelivery count

**Business Rules:** E-001 through E-009

**Relationships:**
- A Delivery **belongs to** one Order (1:1)
- A Delivery **is performed by** one Shopper (N:1)
- A Delivery **is received by** one Customer (N:1)
- A Delivery **has** many GPS Locations (1:N) (the route trace)

---

### 2.13 Dispute

| Attribute | Detail |
|-----------|--------|
| **Purpose** | A formal issue report requiring resolution — covers item discrepancies, cancellations, refunds, and behavioural complaints |
| **Responsibilities** | Follow the unified Dispute Resolution Framework (G-011) lifecycle. Collect evidence, route to appropriate resolution path, record decision, and trigger financial adjustments. |
| **Lifecycle** | `Reported` → `Under Review` → `Evidence Collection` → `Decision` → `Resolved` → `Closed`. Emergency disputes skip to manual review. |
| **Ownership** | Platform (resolution authority) |

**Key Attributes:**
- Dispute ID (UUID)
- Order ID (FK)
- Reported by (Customer / Shopper / System)
- Dispute type (Item Discrepancy / Cancellation / Refund / Shopper Behaviour / Payment Failure / Other)
- Severity (Minor / Moderate / Major)
- Description (free text)
- Evidence references (photos, chat logs, GPS data, receipt)
- Resolution path (Automated / Manual / Escalated)
- Assigned to (Admin ID — if manual)
- Decision (Approved / Rejected / Partial Resolution)
- Decision rationale
- Refund amount (if applicable)
- Goodwill amount (if applicable, discretionary per G-009)
- Resolution timestamp
- Root cause (for analytics)

**Business Rules:** G-006 through G-011, K-006, K-007

**Relationships:**
- A Dispute **belongs to** one Order (N:1)
- A Dispute **may reference** specific Order Items (N:M)
- A Dispute **is resolved by** one Administrator (N:0..1)
- A Dispute **triggers** Payment Transactions (refunds, adjustments) (1:N)

---

### 2.14 Rating

| Attribute | Detail |
|-----------|--------|
| **Purpose** | A user's evaluation of their experience with another user after an order — primary quality signal for the platform |
| **Responsibilities** | Capture feedback, feed into Trust Scores, inform assignment priority, and enable accountability |
| **Lifecycle** | `Pending` (prompt shown, not yet submitted) → `Submitted` → `Revealed` (after both rate OR 72h) → May be `Excluded` (fraudulent) |
| **Ownership** | Platform |

**Key Attributes:**
- Rating ID (UUID)
- Order ID (FK)
- Rater ID (FK to Customer or Shopper)
- Ratee ID (FK to Customer or Shopper)
- Rating type (Customer→Shopper / Shopper→Customer)
- Score (1-5 stars)
- Written feedback (optional)
- Criteria breakdown (per H-001/H-002 — item accuracy, quality, timeliness, communication, professionalism, etc.)
- Status (Pending / Submitted / Revealed / Excluded)
- Exclusion reason (if applicable)
- Submitted timestamp

**Business Rules:** H-001 through H-008

**Relationships:**
- A Rating **belongs to** one Order (N:1)
- A Rating **is given by** one User (Customer or Shopper) (N:1)
- A Rating **is about** one User (Customer or Shopper) (N:1)

---

### 2.15 Notification

| Attribute | Detail |
|-----------|--------|
| **Purpose** | A platform-generated message delivered to a user to inform them of an event or action required |
| **Responsibilities** | Deliver timely, relevant information to users through their preferred channels. Track delivery and read status. |
| **Lifecycle** | `Created` → `Queued` → `Sent` → `Delivered` → `Read` / `Failed` |
| **Ownership** | Platform |

**Key Attributes:**
- Notification ID (UUID)
- Recipient type (Customer / Shopper / Admin)
- Recipient ID (FK)
- Channel (Push / SMS / In-App)
- Notification type (Order Status / Assignment / Payment / Dispute / Promotional / System / Emergency)
- Title
- Body
- Priority (Normal / High / Emergency)
- Related entity type (Order / Payment / etc.)
- Related entity ID
- Status (Created / Queued / Sent / Delivered / Read / Failed)
- Failure reason (if failed)
- Created timestamp
- Read timestamp (if read)

**Business Rules:** A-007, J-003, J-004, J-008

**Relationships:**
- A Notification **targets** one User (Customer, Shopper, or Admin) (N:1)
- A Notification **may reference** one Order (N:0..1)
- A Notification **may reference** one Payment (N:0..1)

---

### 2.16 Promotion

| Attribute | Detail |
|-----------|--------|
| **Purpose** | A marketing incentive that modifies prices or provides credits to influence user behaviour |
| **Responsibilities** | Define discount rules, validate eligibility, apply to qualifying orders, track usage, and measure effectiveness |
| **Lifecycle** | `Draft` → `Active` → `Expired` / `Disabled`. Active promotions are validated against orders in real time. |
| **Ownership** | Platform |

**Key Attributes:**
- Promotion ID (UUID)
- Name
- Type (First Order Discount / Referral Credit / Delivery Fee Discount / Promo Code / Loyalty Reward)
- Discount type (Percentage / Fixed Amount)
- Discount value
- Cap (maximum discount amount)
- Minimum order value
- Applicable zones
- Applicable customer segments
- Usage limit per customer
- Total usage limit
- Validity start date
- Validity end date
- Status (Draft / Active / Expired / Disabled)
- Budget (total allocated spend)
- Actual spend to date

**Business Rules:** F-007, F-008, M-011

**Relationships:**
- A Promotion **applies to** many Orders (1:N)
- A Promotion **generates** Notification events (when applied)

---

### 2.17 Referral

| Attribute | Detail |
|-----------|--------|
| **Purpose** | A customer-to-customer acquisition channel where existing users invite new users to the platform |
| **Responsibilities** | Track the referral link, link the new customer to the referrer, and trigger rewards when the referred customer completes a paid order |
| **Lifecycle** | `Sent` → `Link Clicked` → `Referred Registered` → `Referred Ordered` → `Referred Paid` → `Rewarded` / `Expired` |
| **Ownership** | Platform |

**Key Attributes:**
- Referral ID (UUID)
- Referrer Customer ID (FK)
- Referred Customer ID (FK, nullable until registration)
- Referral code (unique)
- Referral channel (Shareable link / SMS / WhatsApp)
- Status
- Reward status (Pending / Paid / Expired)
- Referrer reward amount (5,000 TZS)
- Referred reward amount (2,500 TZS)
- Created timestamp
- Reward paid timestamp

**Business Rules:** F-008

**Relationships:**
- A Referral **involves** one Referrer Customer (N:1)
- A Referral **involves** one Referred Customer (N:0..1)

---

### 2.18 Audit Log

| Attribute | Detail |
|-----------|--------|
| **Purpose** | An immutable record of all significant system events for security, compliance, and operational analysis |
| **Responsibilities** | Record every financial transaction, administrative action, status change, and security event. Provide an unalterable audit trail. |
| **Lifecycle** | Append-only — entries are created but never modified or deleted. Correction requires a new corrective entry. |
| **Ownership** | Platform |

**Key Attributes:**
- Log ID (UUID)
- Event type (Financial / Admin Action / Status Change / Security / System)
- Actor type (System / Admin / Customer / Shopper)
- Actor ID (FK to relevant entity)
- Action description
- Resource type (Order / Payment / User / etc.)
- Resource ID
- Previous value (if applicable)
- New value (if applicable)
- IP address (if human actor)
- User agent (if human actor)
- Timestamp

**Business Rules:** L-002, L-003

**Relationships:**
- An Audit Log **records** actions by any actor (Administrator, Customer, Shopper, System)
- An Audit Log **references** one resource entity (Order, Payment, etc.)

---

### 2.19 Receipt

| Attribute | Detail |
|-----------|--------|
| **Purpose** | Evidence of the shopper's purchase at the market — used for billing verification and dispute resolution |
| **Responsibilities** | Provide a verifiable record of what was purchased and at what price. Support multiple receipt formats (itemised, handwritten, multiple, or none). |
| **Lifecycle** | `Pending Upload` → `Uploaded` → `Verified` / `Flagged for Review` → May be referenced in disputes |
| **Ownership** | Order (1:1 — each order has one receipt, which may contain multiple photos) |

**Key Attributes:**
- Receipt ID (UUID)
- Order ID (FK) (1:1)
- Photo references (array — supports multiple photos)
- Receipt type (Itemised / Handwritten / Multiple / No Receipt — manual entry)
- Manual price entries (JSON — item name, quantity, price — used when no receipt)
- Upload timestamp
- Verification status (Pending / Verified / Flagged)
- Flag reason (if flagged by I-004 price verification)
- OCR extracted data (V1)

**Business Rules:** D-008, I-004

**Relationships:**
- A Receipt **belongs to** one Order (1:1)
- A Receipt **may be referenced by** Disputes (1:N)
- A Receipt **supports** Payment finalisation (F-004)

---

### 2.20 GPS Location

| Attribute | Detail |
|-----------|--------|
| **Purpose** | A timestamped geographic coordinate transmitted by a shopper's device during active periods |
| **Responsibilities** | Provide location data for assignment calculations, route tracking, delivery verification, and spoofing detection |
| **Lifecycle** | Transmitted, stored for 30 days, then purged (L-003). Not modified after recording. |
| **Ownership** | Platform (temporary data — 30-day retention) |

**Key Attributes:**
- GPS ID (UUID)
- Shopper ID (FK)
- Order ID (FK, nullable — may be recorded when not on an active order)
- Latitude
- Longitude
- Accuracy (meters)
- Speed (if available)
- Bearing (if available)
- Timestamp
- Shopper state at transmission (Waiting / Offered / Travelling / Shopping / Delivering)

**Business Rules:** C-001, C-010, L-003

**Relationships:**
- A GPS Location **is transmitted by** one Shopper (N:1)
- A GPS Location **may be associated with** one Order (N:0..1)
- GPS Locations **form** a Delivery route trace (N per Delivery)

---

### 2.21 Device

| Attribute | Detail |
|-----------|--------|
| **Purpose** | A physical device (smartphone) used by a customer or shopper to access the platform |
| **Responsibilities** | Enable device-bound authentication, push notification delivery, and fraud detection (duplicate accounts from same device) |
| **Lifecycle** | `Registered` (first login) → `Active` → `Deactivated` (user removes device or security event) |
| **Ownership** | User (the person who owns the device) / Platform (device fingerprint record) |

**Key Attributes:**
- Device ID (UUID)
- User ID (FK to Customer or Shopper)
- User type (Customer / Shopper)
- Device fingerprint (computed — I-001)
- Platform (Android / iOS)
- Device model
- OS version
- App version
- Push notification token
- Is primary device (users may have up to 3 authorised devices)
- Last active timestamp
- Status (Active / Deactivated)

**Business Rules:** I-001, I-008

**Relationships:**
- A Device **belongs to** one User (N:1)
- A User **may have** up to 3 Devices (1:3)

---

### 2.22 Session

| Attribute | Detail |
|-----------|--------|
| **Purpose** | An authenticated user interaction with the platform — tracks login state and enables security enforcement |
| **Responsibilities** | Maintain user authentication state, enforce session timeouts, track login history, and enable forced logout on security events |
| **Lifecycle** | `Created` (login) → `Active` → `Expired` (timeout) / `Terminated` (logout or forced) |
| **Ownership** | User |

**Key Attributes:**
- Session ID (UUID)
- User ID (FK)
- User type (Customer / Shopper / Admin)
- Device ID (FK)
- Auth token (hashed)
- IP address
- Created timestamp
- Last activity timestamp
- Expiry timestamp (30 min inactivity timeout)
- Status (Active / Expired / Terminated)

**Business Rules:** I-008, NFR-SEC-006 (session timeout)

**Relationships:**
- A Session **belongs to** one User (N:1)
- A Session **is associated with** one Device (N:1)

---

## 3. Entity Relationship Diagram

```
┌──────────┐     ┌──────────────┐     ┌──────────────┐
│ Customer │────>│    Order     │<────│   Shopper    │
└──────────┘     └──────────────┘     └──────────────┘
     │                │  │                   │
     │                │  │                   │
     v                v  v                   v
┌──────────┐     ┌──────────────┐     ┌──────────────┐
│ Customer │     │ Order Item   │     │Shopper Wallet│
│ Wallet   │     └──────────────┘     └──────────────┘
│ (Future) │            │                   │
└──────────┘            v                   │
                   ┌──────────┐             │
                   │  Market  │             │
                   └──────────┘             │
                        │                   │
                        v                   │
                   ┌──────────┐             │
                   │   Zone   │             │
                   └──────────┘             │
                                            │
┌──────────┐     ┌──────────────┐           │
│ Payment  │────>│    Order     │           │
│Transaction│    └──────────────┘           │
└──────────┘           │                   │
                       │                   │
                       v                   v
                  ┌──────────────┐     ┌──────────────┐
                  │  Delivery    │     │  GPS Location│
                  └──────────────┘     └──────────────┘
                       │
                       v
                  ┌──────────────┐     ┌──────────────┐
                  │   Dispute    │────>│ Audit Log    │
                  └──────────────┘     └──────────────┘
                       │
                       v
                  ┌──────────────┐     ┌──────────────┐
                  │   Rating     │     │ Notification │
                  └──────────────┘     └──────────────┘
                                            │
                                       ┌──────────┐
                                       │Promotion │
                                       └──────────┘
                                            │
                                       ┌──────────┐
                                       │ Referral │
                                       └──────────┘

┌──────────┐     ┌──────────────┐     ┌──────────────┐
│  Device  │────>│   Session    │     │Administrator │
└──────────┘     └──────────────┘     └──────────────┘
                                            │
                                            v
                                       ┌──────────┐
                                       │Audit Log │
                                       └──────────┘
```

---

## 4. Key Relationship Rules

| # | Rule |
|---|------|
| REL-01 | A Customer may have at most **3 active orders** concurrently (A-006) |
| REL-02 | A Shopper may have at most **1 active order** at any time (B-010, E-006) |
| REL-03 | A Customer may have up to **3 authorised devices** (I-008) |
| REL-04 | A Shopper must have exactly **1 Shopper Wallet** (F-010) |
| REL-05 | An Order has exactly **1 Payment**, **1 Delivery**, and **1 Receipt** (1:1 relationships) |
| REL-06 | An Order belongs to exactly **1 Zone** (derived from the market's zone) (C-007) |
| REL-07 | A Market belongs to exactly **1 Zone** (C-007) |
| REL-08 | A Session is bound to a single Device and a single User (I-008) |
| REL-09 | GPS Location data is retained for **30 days** then purged (L-003) |
| REL-10 | Audit Log entries are **append-only** — never modified or deleted (L-002) |
| REL-11 | A Dispute may trigger Payment Transactions (refunds) but does not own them (G-011) |
| REL-12 | A Rating cannot be modified after submission, only excluded for fraud (H-005) |
| REL-13 | Chat logs between Customer and Shopper are retained for **90 days** (I-010) |
| REL-14 | An Order may transition to Cancelled from Created, Offered, Accepted, Travelling, and Shopping states — each with different financial consequences (D-004, G-001 through G-005) |
| REL-15 | Payment status is tied to Order status — e.g., Captured cannot occur before Delivered (F-004, M-010) |

---

*This document is Phase 2 of the Urban Shopper Platform specification. It feeds into Phase 3 (Business Rules — already complete), Phase 4 (State Machines), and Phase 8 (IEEE 29148 SRS).*
