# Urban Shopper Platform — Master SRS Generation Prompt

> **Document Type:** Business Analysis → SRS Generation  
> **Status:** Complete  
> **Date:** 2026-07-24  
> **Version:** 1.0  
> **Purpose:** This prompt, when provided to an AI or requirements engineer, produces a complete IEEE 29148-2018 compliant Software Requirements Specification for the Urban Shopper Platform.  
> **Cross-Reference:** All preceding analysis documents (`01` through `05`)

---

## How to Use This Prompt

This document is a **self-contained generation prompt** for an IEEE 29148-compliant SRS. To use it:

1. Feed this entire document as the prompt to an AI system capable of generating structured technical documentation.
2. The AI should produce an SRS document covering all sections listed below.
3. The SRS must follow IEEE 29148-2018 structure and language conventions.
4. All business analysis context is embedded in this prompt — no external research required.

---

## Prompt — Instructions for the SRS Generator

---

# PROMPT START

You are an experienced Software Requirements Engineer. Your task is to generate a complete **IEEE 29148-2018 compliant Software Requirements Specification (SRS)** for the Urban Shopper Platform.

## IMPORTANT — IEEE 29148 Compliance Rules

1. **Language:** Every system requirement MUST use the verb "SHALL" (IEEE 29148 §5.2.5). Use "SHOULD" for desirable but not mandatory requirements. Use "MAY" for optional requirements. Distinguish clearly between these levels.

2. **Structure:** Follow the SRS structure defined in IEEE 29148-2018 §9 (the "recommended" structure). The required main sections are:
   - 1. Scope
   - 2. Referenced Documents
   - 3. Definitions, Acronyms, and Abbreviations
   - 4. System Overview
   - 5. Specific Requirements (Functional + Non-Functional)

3. **Traceability:** Every functional requirement shall include a traceability tag linking it to: (a) the business rule it implements, (b) the use case it supports, (c) the priority (MVP/V1/V2).

4. **Use Cases:** Include complete use case specifications for each major user-system interaction. Each use case must include: ID, Name, Actors, Trigger, Preconditions, Basic Flow, Alternative Flows, Exception Flows, Postconditions, Business Rules.

5. **Business Rules:** Incorporate the business rules from the embedded Business Rules Analysis section. Every functional requirement that implements a business rule must reference its Rule ID.

6. **Acceptance Criteria:** Every functional requirement must have testable acceptance criteria.

7. **Non-Functional Requirements:** Must cover: Performance, Scalability, Reliability, Availability, Security, Maintainability, Usability, and Portability. Each must be specific, measurable, and testable.

## OUTPUT

Generate the full SRS document. Use the structure below. Fill every section with detailed, specific content. Do not leave placeholders. Do not say "TBD" unless truly impossible to determine at this stage.

---

# URBAN SHOPPER PLATFORM — IEEE 29148 SOFTWARE REQUIREMENTS SPECIFICATION (GENERATED)

## 1. Scope

### 1.1 System Identification
- **System Name:** Urban Shopper Platform
- **System Type:** Digital marketplace connecting customers with independent shoppers for on-demand purchasing and delivery
- **Version:** 1.0 (SRS for MVP and V1 scope)
- **Date:** [Date of generation]

### 1.2 System Purpose
Describe: Urban Shopper is a GPS-enabled mobile application platform that connects customers with verified independent shoppers. Customers submit shopping requests. The platform automatically assigns the nearest available shopper using a 30-second acceptance cascade. The assigned shopper purchases items from traditional markets and delivers them to the customer.

### 1.3 System Scope
[Incorporate from Release Scope — MVP and V1 features only. Exclude V2 and Future items explicitly.]

### 1.4 Product Perspective
This is a new system (greenfield). It replaces no existing system. It depends on:
- Mobile money provider APIs (M-Pesa, Mixx)
- SMS gateway provider (Africastalking/Twilio)
- Push notification service (Firebase Cloud Messaging)
- GPS/location services (Google Play Services / HMS Core)
- Cloud infrastructure provider (AWS/GCP/Azure)

### 1.5 User Characteristics

[Detail the following user roles from the SVPD:]

**1.5.1 Customer**
- Tech comfort: Moderate to high (smartphone users)
- Language: Swahili and English
- Payment preference: Mobile money (M-Pesa/Mixx) and Cash on Delivery
- Typical usage: 2-3 times per week, during business hours

**1.5.2 Shopper**
- Tech comfort: Moderate (basic smartphone proficiency)
- Language: Swahili primary, English secondary
- Device: Budget Android smartphone
- Connectivity: Mobile data (3G/4G), may have intermittent connectivity
- Transport: Motorcycle, bicycle, or public transport

**1.5.3 Platform Administrator (Operations)**
- Tech comfort: High
- Language: English
- Access: Web-based admin dashboard

**1.5.4 Support Agent**
- Tech comfort: Moderate-High
- Access: Web-based support interface with limited permissions

### 1.6 System Context
[Describe the system's place in the broader ecosystem — a diagram should be referenced showing: Customer App ←→ Urban Shopper Platform Backend ←→ Shopper App, with external connections to M-Pesa API, SMS Gateway, Push Notifications, and Maps Services.]

---

## 2. Referenced Documents

- IEEE 29148-2018 — Systems and Software Engineering — Life Cycle Processes — Requirements Engineering
- Business Rules Analysis (03-business-rules.md)
- Vision & Product Definition (02-vision-product-definition.md)
- Release Scope Definition (05-release-scope.md)
- Business Workflows (04-business-workflows.md)

---

## 3. Definitions, Acronyms, and Abbreviations

| Term | Definition |
|------|-----------|
| Customer | End user who submits shopping requests for delivery |
| Shopper | Independent contractor who accepts orders, shops items, and delivers them |
| Platform | The Urban Shopper digital marketplace system |
| Order | A customer request for one or more items to be shopped and delivered |
| Assignment | The process of matching a customer's order to a shopper via GPS proximity |
| Offer Cascade | Sequential offering of an order to shoppers until one accepts or all decline |
| Pre-authorisation Hold | Temporary reservation of funds in the customer's wallet before payment |
| Escrow | Third-party holding of funds until delivery is confirmed |
| COD | Cash on Delivery — customer pays in cash at delivery |
| M-Pesa / Mixx / Airtel Money | Tanzanian mobile money services |
| EAT | East Africa Time (UTC+3) |
| SAR | Straight-line distance (Haversine formula) |
| OTP | One-Time Password for phone verification |
| SLA | Service Level Agreement |
| KYC | Know Your Customer — identity verification process |
| GPS | Global Positioning System for location tracking |
| PWA | Progressive Web App |

---

## 4. System Overview

[Write a concise overview of the system's major functions, referencing the Business Workflows from the embedded analysis:]

The Urban Shopper Platform comprises:

1. **Customer Mobile Application** — Android (MVP) and iOS (V1) apps for submitting shopping requests, tracking orders, communicating with shoppers, rating service, and managing wallet/payments.

2. **Shopper Mobile Application** — Android (MVP) and iOS (V1) apps for receiving order offers, managing availability, shopping items, uploading receipts, navigating to delivery locations, receiving earnings, and communicating with customers.

3. **Platform Backend** — Cloud-based server system handling: order management, GPS-based assignment engine, payment processing (mobile money), user management, fraud detection, analytics, and all APIs.

4. **Administration Web Dashboard** — Web-based interface for: shopper vetting and approval, dispute resolution, fraud investigation, emergency intervention, platform monitoring, reporting, and user management.

### 4.1 Major System Capabilities

| Capability | Description | Delivery |
|-----------|-------------|----------|
| Customer Registration | Phone-based OTP registration with profile management | MVP |
| Order Creation | Manual item entry with price estimation and substitution preferences | MVP |
| GPS Assignment | Nearest-shopper matching with 30-second acceptance cascade | MVP |
| Shopping Workflow | Item status tracking (found/substituted/unavailable), receipt upload | MVP |
| Delivery Workflow | GPS arrival verification, photo proof of delivery, customer confirmation | MVP |
| Payment Processing | M-Pesa/Mixx wallet, pre-auth holds, 24-hour shopper settlement | MVP |
| Cash on Delivery | Eligible customer cash payment at delivery | MVP |
| Rating System | Two-way ratings with blind period and weighted averages | MVP |
| In-App Chat | Text communication during active orders | MVP |
| Fraud Detection | Rule-based detection of duplicates, velocity, prohibited items | MVP |
| Admin Dashboard | Shopper vetting, dispute resolution, fraud alerts, metrics | MVP |
| Escrow Integration | Third-party fund holding until delivery confirmation | V1 |
| Scheduled Orders | Order scheduling with timed release | V1 |
| Multi-Language | Full Swahili/English UI with i18n framework | V1 |
| Promotions Engine | Admin-configurable promo codes and discounts | V1 |
| Live Map Tracking | Real-time shopper location on map during delivery | V1 |
| Performance Tiers | Shopper classification with differentiated benefits | V1 |

---

## 5. Specific Requirements

### 5.1 Functional Requirements

Each requirement SHALL use "SHALL" (mandatory), "SHOULD" (desirable), or "MAY" (optional).

Use the following structure for each functional requirement:

| Tag | Field | Description |
|-----|-------|-------------|
| FR-[ID] | Requirement ID | Unique identifier |
| | Title | Brief name |
| | Description | Detailed requirement using SHALL/SHOULD/MAY |
| | Rationale | Business justification |
| | Priority | MVP / V1 / V2 |
| | Business Rules | Rule IDs from the Business Rules Analysis |
| | Use Case | Related use case ID |
| | Acceptance Criteria | Testable conditions that satisfy the requirement |

#### 5.1.1 Customer Registration & Account Management

Define requirements FR-CUST-001 through FR-CUST-015 covering:

- Phone number registration with OTP verification
- Minimum age enforcement (18+)
- Language and notification preference selection
- Account deactivation (customer-initiated)
- Account suspension (platform-initiated)
- Dormant account cleanup after 12 months
- Data retention and purging
- Max 3 active orders per customer
- Terms of Service acceptance and re-acceptance
- Device binding and session management
- Login rate limiting (max 3 OTP requests per hour)
- Account recovery flow

#### 5.1.2 Shopper Registration & Management

Define requirements FR-SHOP-001 through FR-SHOP-020 covering:

- Registration with minimum requirements (18+, valid ID, smartphone, transport, mobile money)
- ID verification with photo upload and facial matching
- Background check consent and processing
- Transport documentation verification (licence, registration, insurance)
- Onboarding training module (content, quiz, pass/fail)
- Go Online/Offline status management
- Auto-offline after 15 minutes without GPS ping
- Shopper status lifecycle (Pending → Active → Suspended → Deactivated)
- Shopper suspension criteria and process
- Shopper appeal process
- Performance tier calculation (order volume, acceptance rate, completion rate, rating)
- Minimum monthly activity requirement (10 orders/month)
- Concurrent order limit (1 order at a time)
- Acceptance rate tracking over rolling 30-day window
- Working hours configuration (06:00-22:00 default)

#### 5.1.3 GPS & Assignment Engine

Define requirements FR-GPS-001 through FR-GPS-015 covering:

- Shopper GPS ping frequency (every 10 seconds when Active)
- GPS signal loss handling (use last known, timestamped; auto-offline after 2 minutes)
- Distance calculation using Haversine formula
- Nearest-available shopper selection
- Fairness distribution across shoppers (no single shopper > 20% of zone orders in 7 days)
- 30-second acceptance window with countdown timer
- Offer cascade to next nearest on decline/timeout
- Maximum cascade duration (5 minutes)
- Maximum offer radius in dense urban (5 km) and less dense (10 km)
- Dynamic radius expansion when supply is low
- Customer location privacy (concealed until acceptance)
- GPS spoofing detection (impossible speed, location jumps, static location)
- Geographic coverage zone definitions and management
- Order expiration when no shopper accepts

#### 5.1.4 Order Management

Define requirements FR-ORD-001 through FR-ORD-025 covering:

- Order creation with minimum required fields
- Manual item entry with predictive text
- Order status lifecycle (Created → Offered → Accepted → Shopping → In Delivery → Delivered → Completed)
- Status transition logging with timestamps
- Order pricing breakdown display (item estimate, service fee, delivery fee, total)
- Pre-authorisation hold on customer wallet
- Order acceptance validation (shopper must be within zone)
- Arrival at market confirmation with SLA (10-minute window)
- Item status tracking per item (Found / Substituted / Not Available)
- Substitution handling per customer preference (Best Match / Contact Me / No Substitutions)
- Contact Me substitution with 3-minute customer response window and auto-proceed fallback
- Receipt photo upload and storage
- Manual price entry fallback when vendor does not provide itemised receipt
- Final cost calculation and variance approval (> 10% requires customer approval)
- Order cancellation (customer-initiated) with status-dependent fees
- Shopper cancellation (limited to valid reasons only)
- Platform-initiated cancellation
- Order expiration (unaccepted after 5 minutes, customer notified with options)
- Shopping time monitoring (60 min → flag, 90 min → support alert)
- Scheduled order release (offered 30 minutes before delivery window)
- Large order surcharge (30+ items: +5%)
- Order archival after 90 days
- Abandoned order detection (shopper unreachable 30+ minutes)

#### 5.1.5 Delivery & Fulfillment

Define requirements FR-DEL-001 through FR-DEL-015 covering:

- Delivery radius limit (10 km standard, longer with additional fee)
- GPS arrival verification (shopper must be within 100m of delivery address)
- Proof of delivery (photo + customer confirmation)
- Delivery time SLA (30 min within 5 km, 45 min for 5-10 km)
- Delivery delay monitoring and customer notification
- Unavailable customer procedure (10 min wait, call, chat, then leave or return)
- Wrong address procedure (correctable nearby → deliver; significantly different → additional fee or cancel)
- Item inspection window (15 minutes standard, 5 minutes for perishables)
- Redelivery on failed first attempt (70% of original delivery fee)
- Single-order per trip rule (no batching in MVP)
- Leave-at-door delivery (customer pre-authorisation required)
- Customer no-show escalation to support

#### 5.1.6 Payments & Financial

Define requirements FR-PAY-001 through FR-PAY-020 covering:

- Customer wallet creation and management
- Wallet deposit via M-Pesa and Mixx API
- Pre-authorisation hold on wallet for estimated order total
- Insufficient balance handling (retry, switch to COD, or cancel)
- Final payment calculation from actual item costs
- Variance approval workflow (> 10% requires customer approval)
- Platform service fee calculation (15% of item cost, min 1,000 TZS, max 20,000 TZS)
- Delivery fee calculation (1,500 TZS base + 500 TZS/km, max 10,000 TZS)
- Shopper payout calculation (delivery fee + 5% shopping commission)
- Shopper settlement period (24 hours standard, 48 hours for new shoppers)
- Shopper wallet with pending/available balance and withdrawal
- Failed payment retry logic (max 2 retries within 5 minutes)
- Cash-on-Delivery with eligibility criteria (5+ orders, no disputes, max 100,000 TZS)
- Cash handling fee (2% for shopper on COD orders)
- First-order delivery fee promotion (50% off, capped 3,000 TZS)
- Promotional credits management
- Referral programme credits (10,000 TZS referrer, 5,000 TZS referee)
- Financial audit logging (all transactions immutably logged)

#### 5.1.7 Cancellations & Refunds

Define requirements FR-CAN-001 through FR-CAN-012 covering:

- Pre-acceptance cancellation (full release of hold, no fee)
- Post-acceptance, pre-shopping cancellation (50% delivery fee to shopper)
- Post-acceptance, en-route > 15 min (75% delivery fee to shopper)
- Post-shopping-start cancellation (full delivery fee + 10% restocking fee)
- Shopper-initiated cancellation (limited to valid reasons only)
- Platform-initiated cancellation (no penalty to any party)
- Full refund on platform error
- Partial refund for item issues (value of problematic items + 20% of delivery fee)
- Refund processing timeline (2 hours to wallet, 24 hours external transfer)
- Refund dispute process with escalation
- Abandoned order handling (shopper unreachable > 30 min)
- Restocking fee for cancelled orders where items were purchased

#### 5.1.8 Ratings & Quality

Define requirements FR-RATE-001 through FR-RATE-010 covering:

- Customer rating of shopper (1-5 stars, optional written feedback)
- Shopper rating of customer (1-5 stars, not publicly displayed)
- Weighted average rating calculation (last 10: 40%, 11-30: 30%, 31-100: 30%)
- Minimum rating threshold for shoppers (3.5 average)
- Rating fraud detection (pattern analysis, exclusion of fraudulent ratings)
- Rating blind period (hidden until both rate or 72 hours elapsed)
- Quality spot check sampling (1% of completed orders weekly)
- Rating prompt timing (immediately after order completion)
- Rating submission window (within 72 hours)

#### 5.1.9 Communication & Notifications

Define requirements FR-COMM-001 through FR-COMM-012 covering:

- In-app chat between customer and shopper (active order + 2 hours)
- Chat archival after 2 hours post-delivery (or when dispute is resolved)
- Masked phone calling between customer and shopper (V1)
- Order offer notification to shopper (push + in-app + SMS fallback)
- Order status notifications to customer at each transition
- Shopper response SLA (5 minutes for chat responses)
- Prohibited communication detection (phone numbers, external payment requests, abuse)
- Language preference for communication display
- SMS fallback for feature phone customers (V1)
- Logging of all communication for dispute resolution (90-day retention)
- Customer notification preferences (push, SMS, in-app)
- Emergency notifications bypass preference settings

#### 5.1.10 Fraud Detection & Security

Define requirements FR-FRAUD-001 through FR-FRAUD-015 covering:

- Duplicate account detection (phone number, device fingerprint, national ID)
- Transaction velocity monitoring (> 5 orders to different locations in 60 min → flag)
- Order acceptance location validation (shopper GPS must be within zone)
- Receipt price verification against market average database
- High-value order verification (> 200,000 TZS requires additional OTP)
- COD risk management (limited to eligible customers)
- Prohibited items detection (keyword matching in item lists)
- Account takeover prevention (device binding, login alerts, rate limiting)
- Shopper bond/deposit (V1 — 20,000-50,000 TZS)
- Communication recording and retention
- Mule account detection (payment proxy patterns)
- GPS spoofing detection (velocity, location jumps, static movement)
- Automated fraud alert triage
- Alert severity classification and escalation

#### 5.1.11 Administration & Operations

Define requirements FR-ADMIN-001 through FR-ADMIN-020 covering:

- Tiered admin access (Super Admin, Operations Admin, Support Agent)
- Shopper application review and approval workflow
- Order management (search, filter, manual intervention)
- Dispute resolution workflow with evidence review
- Fraud investigation workflow
- Emergency intervention workflow
- Platform monitoring dashboard (live metrics)
- Financial audit log with immutability
- Automated report generation (daily, weekly, monthly)
- SLA monitoring and breach alerting
- Zone management (activate/deactivate, radius settings)
- User lookup by ID/phone/name
- User account suspension/restore
- Platform maintenance window enforcement
- Business continuity controls (data backup schedule)
- Regulatory reporting data export

### 5.2 Use Cases

Define each use case with the following structure. Cover ALL major workflows from the Business Workflows analysis.

#### Use Case UC-001: Customer Registration

| Field | Value |
|-------|-------|
| ID | UC-001 |
| Name | Customer Registration |
| Actors | Prospective Customer (primary), SMS Gateway (secondary) |
| Trigger | Customer taps "Register" in the app |
| Preconditions | Customer has smartphone, valid Tanzanian phone number, and mobile money account |
| Basic Flow | 1. Customer enters phone number. 2. System sends OTP via SMS. 3. Customer enters OTP. 4. System validates OTP. 5. Customer enters name and selects language/notification preferences. 6. Customer accepts Terms of Service. 7. System creates account. 8. System displays onboarding tutorial. |
| Alternative Flows | 1a. OTP not received after 60s → resend with voice call fallback. 3a. OTP expired → request new OTP. 6a. Terms updated since last visit → require re-acceptance. |
| Exception Flows | 2a. SMS gateway failure → log and retry with alternative provider. 4a. Duplicate phone → offer account recovery. 7a. Duplicate device → flag for review, create limited account. |
| Postconditions | Account created with "Active" status. Customer can browse and create orders. First-order promotion activated. |
| Business Rules | A-001, A-003, A-007, I-001, L-007 |

[Repeat this structure for all major workflows:]
- UC-002: Customer Order Submission
- UC-003: Customer Track Order
- UC-004: Customer Receive Delivery
- UC-005: Customer Cancel Order
- UC-006: Customer Request Refund/Dispute
- UC-007: Shopper Registration & Verification
- UC-008: Shopper Go Online & Receive Offer
- UC-009: Shopper Accept & Navigate to Market
- UC-010: Shopper Shop Items & Handle Substitutions
- UC-011: Shopper Checkout & Upload Receipt
- UC-012: Shopper Deliver to Customer
- UC-013: Shopper View Earnings & Withdraw
- UC-014: Admin Approve/Reject Shopper Application
- UC-015: Admin Resolve Dispute
- UC-016: Admin Investigate Fraud Alert
- UC-017: Admin Handle Emergency
- UC-018: Admin Monitor Platform Metrics

### 5.3 Non-Functional Requirements

#### 5.3.1 Performance Requirements

| ID | Requirement | Target | Acceptance Criteria |
|----|-------------|--------|-------------------|
| NFR-PERF-001 | The system SHALL complete the order acceptance handling (offer → accept/decline) in under 500ms for 95% of offers. | < 500ms | Load test with 1,000 concurrent shoppers receiving offers simultaneously. 95th percentile under 500ms. |
| NFR-PERF-002 | The assignment engine SHALL process the offer cascade (decline → next shopper) in under 200ms for 99% of cascades. | < 200ms | Measure time between shopper decline and next shopper receiving offer. |
| NFR-PERF-003 | The customer app SHALL display the order tracking screen with current status in under 2 seconds for 95% of requests. | < 2s | Load test with 500 concurrent users refreshing tracking screens. |
| NFR-PERF-004 | The system SHALL process a mobile money payment transaction in under 10 seconds from initiation to confirmation. | < 10s | End-to-end payment flow timing, including external API call to M-Pesa. |
| NFR-PERF-005 | The admin dashboard SHALL load with current metrics (orders, shoppers, disputes) in under 3 seconds. | < 3s | Dashboard page load time with 10,000+ orders in the database. |
| NFR-PERF-006 | The shopper app SHALL process GPS location pings with no more than 2-second delay from capture to server receipt. | < 2s | GPS ping latency measurement over a 1-hour active session. |

#### 5.3.2 Scalability Requirements

| ID | Requirement | Target | Acceptance Criteria |
|----|-------------|--------|-------------------|
| NFR-SCAL-001 | The system SHALL support horizontal scaling of the backend API tier without requiring application code changes. | Stateless API | Deploy additional API server instances; verify no session affinity required. |
| NFR-SCAL-002 | The system SHALL handle 10x the projected MVP launch load (10,000 concurrent users, 500 concurrent shoppers, 100 concurrent orders) without degradation. | 10x MVP load | Load test to 10,000 concurrent users with normal response times. |
| NFR-SCAL-003 | The database SHALL support sharding by geographic zone to enable independent scaling of high-density areas. | Zone-based sharding | Add a new shard for a new zone; verify data isolation and query routing. |
| NFR-SCAL-004 | The system SHALL support adding new geographic zones without requiring a system downtime or restart. | Zero-downtime zone add | Add a new zone configuration; verify existing operations unaffected. |

#### 5.3.3 Reliability & Availability Requirements

| ID | Requirement | Target | Acceptance Criteria |
|----|-------------|--------|-------------------|
| NFR-REL-001 | The platform SHALL maintain 99.5% uptime during core operating hours (06:00-22:00 EAT), excluding scheduled maintenance. | 99.5% uptime | Monitor uptime over 90-day period. Maximum allowed downtime: ~2.7 hours/month during operating hours. |
| NFR-REL-002 | The system SHALL automatically fail over to a secondary data centre within 5 minutes of primary failure detection. | 5 min failover | Chaos engineering: terminate primary server. Verify secondary takes over within 5 min. |
| NFR-REL-003 | The mobile money payment flow SHALL retry failed transactions up to 2 times automatically before failing. | 2 retries | Simulate M-Pesa API timeout; verify retry occurs and succeeds or fails gracefully. |
| NFR-REL-004 | The customer app SHALL queue order status updates locally when the network is unavailable and sync when connectivity resumes. | Offline queue | Kill network mid-order; verify status updates are queued and transmitted on reconnection. |
| NFR-REL-005 | The shopper app SHALL continue to display current order information from cached data for at least 15 minutes after network loss. | 15 min offline | Enable airplane mode on shopper device; verify order details accessible for 15 minutes. |
| NFR-REL-006 | The system SHALL back up all data hourly (incremental) and daily (full) with backups stored in a geographically separate location. | Hourly + daily backup | Verify backup creation and successful restore to staging environment within 2 hours. |

#### 5.3.4 Security Requirements

| ID | Requirement | Target | Acceptance Criteria |
|----|-------------|--------|-------------------|
| NFR-SEC-001 | All API communications SHALL be encrypted using TLS 1.2 or higher. | TLS 1.2+ | SSL/TLS scan of all endpoints; no protocol below TLS 1.2 accepted. |
| NFR-SEC-002 | All user passwords SHALL be stored using bcrypt (cost factor 12) or equivalent hashing algorithm. | bcrypt cost 12 | Password audit; verify no plaintext or weak hashing in storage. |
| NFR-SEC-003 | All personally identifiable information (PII) SHALL be encrypted at rest using AES-256. | AES-256 | Database encryption audit; verify PII fields encrypted. |
| NFR-SEC-004 | The system SHALL implement OTP-based two-factor authentication for: (a) account registration, (b) password reset, (c) high-value order confirmation (> 200,000 TZS). | 2FA for critical actions | Verify OTP is required for each listed action and cannot be bypassed. |
| NFR-SEC-005 | The system SHALL implement rate limiting: max 3 OTP requests per phone number per hour, max 5 login attempts per account per 15 minutes. | Rate limiting | Automated test: exceed thresholds; verify temporary block is enforced. |
| NFR-SEC-006 | The system SHALL enforce session timeout after 30 minutes of inactivity for all user-facing apps. | 30 min timeout | Leave app idle for 31 minutes; verify session expired and re-authentication required. |
| NFR-SEC-007 | The system SHALL log all administrative actions with admin ID, timestamp, action type, and affected entity. | Full admin audit trail | Verify log entries for every admin action type. |
| NFR-SEC-008 | The system SHALL not transmit full mobile money account numbers in API responses or logs. | Masked identifiers | Audit API responses and logs; verify mobile money numbers are masked or tokenised. |

#### 5.3.5 Usability Requirements

| ID | Requirement | Target | Acceptance Criteria |
|----|-------------|--------|-------------------|
| NFR-UX-001 | The customer app SHALL support completion of the entire order placement flow (item entry → submission) in no more than 6 taps/screens for a 5-item order. | ≤ 6 screens | UX audit and testing with 10 users; measure screens to completion. |
| NFR-UX-002 | The customer app SHALL display all text in both Swahili and English with automatic language detection based on device settings. | Bilingual | Switch device language; verify app text switches accordingly. |
| NFR-UX-003 | The shopper app SHALL display the 30-second offer timer with a prominent countdown and vibration alert. | Visible+vibration | Verify countdown displays on screen and device vibrates on offer receipt. |
| NFR-UX-004 | The total app size (APK) SHALL not exceed 25 MB to accommodate users with limited storage and data budgets. | ≤ 25 MB APK | Measure APK size; ensure no unnecessary assets bundled. |
| NFR-UX-005 | The app SHALL function acceptably on devices with 1 GB RAM running Android 8.0 (API level 26) or higher. | Low-end device support | Test on a reference device (Moto E5 or equivalent) with 1 GB RAM. |
| NFR-UX-006 | The app SHALL use no more than 5 MB of mobile data per 10-minute session of normal usage (tracking, chatting, refreshing status). | ≤ 5 MB / 10 min | Profile app data usage over a standard session. |

#### 5.3.6 Maintainability Requirements

| ID | Requirement | Target | Acceptance Criteria |
|----|-------------|--------|-------------------|
| NFR-MAINT-001 | The system SHALL follow a modular architecture with clearly separated layers: API gateway, application services, data access, external integrations. | Layered architecture | Architecture review; verify dependencies flow in one direction only. |
| NFR-MAINT-002 | The system SHALL expose a versioned REST API (e.g., /api/v1/) for all public endpoints with backward compatibility for at least two minor versions. | API versioning | Verify /api/v1/ and /api/v2/ can coexist. Deprecation notice for v1 before removal. |
| NFR-MAINT-003 | The codebase SHALL maintain test coverage of at least 80% for business logic layers and 70% for UI code. | ≥ 80% logic, ≥ 70% UI | Measure coverage; CI pipeline must enforce minimum coverage thresholds. |
| NFR-MAINT-004 | The system SHALL support feature flags to enable/disable features without code deployment. | Feature flags | Toggle a feature flag; verify feature activates/deactivates without restart. |
| NFR-MAINT-005 | All third-party API integrations (M-Pesa, Mixx, SMS, push notifications) SHALL be behind an abstraction layer that allows swapping providers with configuration changes only. | Provider abstraction | Simulate M-Pesa provider swap via config; verify no code changes needed. |

#### 5.3.7 Data Requirements

| ID | Requirement | Target | Acceptance Criteria |
|----|-------------|--------|-------------------|
| NFR-DATA-001 | The system SHALL store customer personal data for the duration of the active account plus 3 years after deactivation. | Active + 3 years | Verify data deletion scripts and retention configuration. |
| NFR-DATA-002 | The system SHALL store transactional data for 7 years for tax and legal compliance. | 7 years | Verify archival strategy and retrieval capability. |
| NFR-DATA-003 | The system SHALL store chat logs for 90 days from order completion. | 90 days | Verify chat log expiry and deletion process. |
| NFR-DATA-004 | The system SHALL store GPS tracking data for 30 days. | 30 days | Verify GPS data retention configuration. |
| NFR-DATA-005 | The system SHALL support GDPR-equivalent data subject rights: access, rectification, erasure, and portability upon request. | Data subject rights | Implement and test data export and account deletion flows. |

### 5.4 Constraints

| ID | Constraint | Impact |
|----|------------|--------|
| CON-001 | The platform shall operate primarily through mobile money (M-Pesa, Mixx, Airtel Money). Traditional credit card processing shall NOT be the primary payment method. | Payment architecture must be built around mobile money APIs first. |
| CON-002 | No standardised addressing system exists in Tanzanian cities. The platform must use GPS coordinates, landmark descriptions, and in-app pin-drop for delivery addresses. | Customer must verify address accuracy. Expect address-related edge cases in delivery. |
| CON-003 | 36% smartphone penetration and 22 million 2G-only users in Tanzania. | App must be lightweight, data-efficient. PWA/USSD fallback needed in V1. |
| CON-004 | Mobile internet speeds are improving but variable. Median speed 22.61 Mbps with significant rural/urban divide. | App must function on 3G, recover from 2G, support offline queuing. |
| CON-005 | Mobile money transaction limits (e.g., M-Pesa daily limit ~3M TZS) may constrain high-value orders. | High-value order workflow must account for payment provider limits. |
| CON-006 | Regulatory uncertainty around gig worker classification. No current specific legislation, but may emerge. | Platform architecture must support both contractor and employee models without major rework. |

### 5.5 Assumptions and Dependencies

| ID | Assumption/Dependency | Risk if False |
|----|-----------------------|---------------|
| DEP-001 | M-Pesa and Mixx APIs will remain available with documented SLAs throughout the platform lifecycle. | Platform cannot process payments — complete operational failure. |
| DEP-002 | 4G coverage in Dar es Salaam is sufficient for real-time GPS tracking and messaging. | Order tracking and communication degrade, reducing customer trust. |
| DEP-003 | Sufficient supply of prospective shoppers with smartphones and transport in target zones. | Cannot fulfill orders — demand outpaces supply. |
| DEP-004 | Customers will accept a 15-25% service fee on orders. | Low conversion rate — customers unwilling to pay for convenience. |
| DEP-005 | Tanzanian regulatory environment remains permissive for gig economy platforms. | Legal challenges force costly operational changes. |

### 5.6 Traceability Matrix

[Create a matrix mapping:]
- Functional Requirements → Business Rules
- Functional Requirements → Use Cases
- Functional Requirements → Release (MVP/V1/V2)
- Business Rules → Use Cases

Example format:

| Requirement | Business Rules | Use Cases | Release | Priority |
|-------------|---------------|-----------|---------|----------|
| FR-CUST-001 | A-001, A-003, I-001 | UC-001 | MVP | High |
| FR-CUST-002 | A-001, A-007 | UC-001 | MVP | Medium |
| FR-GPS-001 | C-001 | UC-008 | MVP | Critical |
| ... | ... | ... | ... | ... |

### 5.7 Interface Requirements

#### 5.7.1 User Interfaces

- Customer mobile app: Android native (MVP), iOS native (V1)
- Shopper mobile app: Android native (MVP), iOS native (V1)
- Admin dashboard: Web-based responsive interface
- PWA (V1): Browser-based ordering for feature phone users
- USSD (V1): Basic order status checking via USSD codes

#### 5.7.2 Hardware Interfaces

- GPS receiver (via device GPS chipset)
- Camera (for receipt photo, ID upload, delivery confirmation photo)
- Vibration motor (for offer notification)
- Network interface (mobile data, WiFi)

#### 5.7.3 Software Interfaces

| External System | Interface Type | Data | Protocol |
|-----------------|----------------|------|----------|
| M-Pesa API | REST API | Payment requests, confirmations, balances, withdrawals | HTTPS/JSON |
| Mixx API | REST API | Payment requests, confirmations, balances, withdrawals | HTTPS/JSON |
| Airtel Money API (V1) | REST API | Payment processing | HTTPS/JSON |
| SMS Gateway (Africastalking) | REST API | OTP delivery, notifications | HTTPS/JSON |
| Firebase Cloud Messaging | REST API | Push notifications | HTTPS/JSON |
| Google Maps / OpenStreetMap | SDK | Maps display, geocoding, routing | SDK/API |
| Google Play Services / HMS | SDK | Location services | SDK |

---

## PROMPT END

---

## SRS Generation Instructions for the AI

1. **Use the embedded business analysis context.** All rules, workflows, definitions, and scope decisions are provided above. Do not invent new business rules or requirements that contradict them.

2. **Apply IEEE 29148 language discipline.** Every functional requirement uses SHALL. Distinguish between MUST, SHOULD, and MAY correctly.

3. **Be specific and testable.** "The system shall be fast" is not a requirement. "The system SHALL respond to order acceptance in under 500ms for 95% of requests" is a requirement.

4. **Complete traceability.** Every functional requirement in section 5.1 must have a corresponding entry in the traceability matrix (section 5.6). Every use case must have its business rules field populated.

5. **Cover ALL business rules.** The 133 business rules in the Business Rules Analysis must each be implemented by at least one functional requirement. Create a validation check.

6. **Cover ALL workflows.** The use cases must map to every workflow in the Business Workflows document.

7. **Generate the output as a complete document.** Start with the title page and table of contents. Include all sections. Do not leave any section incomplete with "TBD."

8. **Use consistent tagging.** Follow the ID naming conventions:
   - Functional Requirements: FR-[AREA]-[NNN]
   - Use Cases: UC-[NNN]
   - Non-Functional Requirements: NFR-[CATEGORY]-[NNN]
   - Business Rules: [Category Letter]-[NNN] (as defined in Business Rules Analysis)

---

*This Master SRS Prompt is the final deliverable of the Urban Shopper Platform Business Analysis phase. It synthesizes all preceding analysis and is ready for SRS generation.*
