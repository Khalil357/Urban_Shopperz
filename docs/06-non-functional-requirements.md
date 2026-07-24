# Urban Shopper Platform — Non-Functional Requirements Specification

> **Document Type:** Technical Specification  
> **Status:** Complete  
> **Date:** 2026-07-24  
> **Version:** 1.0  
> **Cross-Reference:** Business Rules — `03-business-rules.md`, System Architecture — (future), IEEE 29148 SRS — (future)  
> **Phase:** 6 of 12

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Performance Requirements](#2-performance-requirements)
3. [Scalability Requirements](#3-scalability-requirements)
4. [Availability & Reliability Requirements](#4-availability--reliability-requirements)
5. [Security Requirements](#5-security-requirements)
6. [Data Requirements](#6-data-requirements)
7. [Usability & Accessibility Requirements](#7-usability--accessibility-requirements)
8. [Localisation & Internationalisation Requirements](#8-localisation--internationalisation-requirements)
9. [Compliance Requirements](#9-compliance-requirements)
10. [Disaster Recovery & Business Continuity](#10-disaster-recovery--business-continuity)
11. [Monitoring, Logging & Auditability](#11-monitoring-logging--auditability)
12. [Maintainability Requirements](#12-maintainability-requirements)
13. [Interoperability Requirements](#13-interoperability-requirements)
14. [Traceability Matrix](#14-traceability-matrix)

---

## 1. Introduction

### 1.1 Purpose

This document specifies the non-functional requirements (NFRs) for the Urban Shopper Platform. These requirements define the system's quality attributes — how well the system performs, not what it does. Each requirement is specific, measurable, achievable, relevant, and testable.

### 1.2 Scope

These NFRs apply to all components of the Urban Shopper Platform: Customer Mobile App, Shopper Mobile App, Platform Backend APIs, Assignment Engine, Admin Dashboard, and all supporting infrastructure.

### 1.3 Conventions

Each requirement uses the IEEE 29148 convention:

| Verb | Meaning |
|------|---------|
| **SHALL** | Mandatory requirement |
| **SHOULD** | Desirable requirement (implement if feasible) |
| **MAY** | Optional requirement |

---

## 2. Performance Requirements

### 2.1 API Response Times

| ID | Requirement | Target | Measurement Method | Priority |
|----|-------------|--------|-------------------|----------|
| NFR-PERF-001 | The system SHALL complete the order acceptance workflow (offer → accept/decline processing) in under **500ms** for 95% of offers, measured from server receipt of offer event to server dispatch of acceptance confirmation. | < 500ms (p95) | Load test: 1,000 concurrent shoppers receiving offers simultaneously | Critical |
| NFR-PERF-002 | The assignment engine SHALL process the offer cascade (shopper decline → next shopper offer) in under **200ms** for 99% of cascades. | < 200ms (p99) | Measure time from decline event to next shopper notification dispatch | Critical |
| NFR-PERF-003 | The customer order tracking screen SHALL load with current status in under **2 seconds** for 95% of requests under normal load. | < 2s (p95) | Load test: 500 concurrent users refreshing tracking screens | High |
| NFR-PERF-004 | The admin dashboard SHALL load with current metrics in under **3 seconds** with 10,000+ orders in the database. | < 3s | Page load timing with realistic data volume | High |
| NFR-PERF-005 | The system SHALL respond to GPS location pings with under **200ms** server-side processing time for 99% of pings. | < 200ms (p99) | Measure server processing time per GPS ping | Critical |
| NFR-PERF-006 | The in-app chat message SHALL be delivered from sender to recipient in under **1 second** for 95% of messages during normal operation. | < 1s (p95) | End-to-end message delivery timing | High |
| NFR-PERF-007 | The search/suggestion API for item entry SHALL return results in under **500ms** for 95% of queries. | < 500ms (p95) | Load test: item search queries | Medium |

### 2.2 Payment Processing

| ID | Requirement | Target | Measurement Method | Priority |
|----|-------------|--------|-------------------|----------|
| NFR-PERF-008 | The mobile money pre-authorisation flow SHALL complete in under **10 seconds** from initiation to confirmation (including external M-Pesa/Mixx API call time) for 90% of requests. | < 10s (p90) | End-to-end payment timing including external API | Critical |
| NFR-PERF-009 | The payment retry mechanism (F-011) SHALL complete all 2 retry attempts within **5 minutes**. | Complete within 5 min | Automated test simulating payment failure | High |
| NFR-PERF-010 | The 48-hour shopper settlement batch SHALL process within **30 minutes** of the scheduled settlement time. | < 30 min batch window | Monitor settlement job execution time | Medium |

### 2.3 Real-Time Processing

| ID | Requirement | Target | Measurement Method | Priority |
|----|-------------|--------|-------------------|----------|
| NFR-PERF-011 | The system SHALL deliver push notifications to shopper devices within **3 seconds** of an offer event for 95% of offers. | < 3s (p95) | End-to-end push delivery timing | Critical |
| NFR-PERF-012 | The shopper GPS location SHALL be reflected on the customer's tracking screen with no more than **5 seconds** of latency from ping receipt. | < 5s latency | Measure time from GPS ping to customer display update | High |
| NFR-PERF-013 | The 30-second offer countdown SHALL be accurate to within ±1 second across all shopper devices. | ±1s accuracy | Client-server clock drift measurement | Critical |

---

## 3. Scalability Requirements

### 3.1 Load Scaling

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-SCAL-001 | The system SHALL support horizontal scaling of the backend API tier by adding instances behind a load balancer, without requiring application code changes. | Stateless API | Deploy additional API server instances; verify no session affinity required | Critical |
| NFR-SCAL-002 | The system SHALL handle **10x the projected MVP launch load** without degradation: 10,000 concurrent users, 500 concurrent shoppers, 100 concurrent orders in flight. | 10x MVP load | Load test to target metrics; verify 95th percentile response times remain within NFR-PERF targets | High |
| NFR-SCAL-003 | The system SHALL handle **100x the projected MVP launch load** for brief spikes (up to 5 minutes) without crashing, though response time degradation is acceptable. | 100x spike load | Stress test with rapid load increase; verify system recovers when load returns to normal | High |
| NFR-SCAL-004 | The assignment engine SHALL support at least **10,000 concurrent shopper connections** for GPS ping reception without dropping connections. | 10,000 concurrent connections | Connection stress test | Critical |
| NFR-SCAL-005 | The system SHALL support adding new geographic zones without requiring a system restart or downtime. | Zero-downtime zone add | Add a new zone configuration; verify existing operations unaffected | High |

### 3.2 Data Scaling

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-SCAL-006 | The database SHALL support sharding by geographic zone to enable independent scaling of high-density areas. | Zone-based sharding | Add a new shard; verify data isolation and query routing | Medium |
| NFR-SCAL-007 | The system SHALL support at least **1,000,000 orders** in the active database before archival performance targets are impacted. | 1M active orders | Benchmark query performance at increasing order volumes | High |
| NFR-SCAL-008 | The GPS location ingestion pipeline SHALL handle at least **1,000 GPS pings per second** across all active shoppers. | 1,000 pings/sec | Load test GPS ingestion endpoint | Critical |

---

## 4. Availability & Reliability Requirements

### 4.1 Uptime

| ID | Requirement | Target | Measurement | Priority |
|----|-------------|--------|-------------|----------|
| NFR-AVL-001 | The platform SHALL maintain **99.5% uptime** during core operating hours (06:00-22:00 EAT), excluding scheduled maintenance windows. Maximum allowed downtime: ~2.7 hours/month. | 99.5% uptime | Monitor uptime over rolling 30-day period | Critical |
| NFR-AVL-002 | The platform SHALL maintain **98% uptime** during off-peak hours (22:00-06:00 EAT), excluding scheduled maintenance. | 98% uptime | Monitor uptime over rolling 30-day period | Medium |
| NFR-AVL-003 | Scheduled maintenance SHALL be limited to the defined window: **03:00-05:00 EAT** (L-006). No more than 2 scheduled maintenance windows per calendar month. | 2 hrs/window, 2x/month | Track maintenance frequency and duration | High |
| NFR-AVL-004 | Emergency maintenance outside the scheduled window SHALL require Super Admin approval and SHALL be communicated to all active users with 1-hour minimum notice where feasible. | Super Admin approval required | Audit log review | High |

### 4.2 Failover & Recovery

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-AVL-005 | The system SHALL automatically fail over to a secondary data centre or availability zone within **5 minutes** of primary failure detection. | RTO: 5 minutes | Chaos engineering: terminate primary server; verify secondary takes over | Critical |
| NFR-AVL-006 | The system SHALL not lose more than **1 minute** of data in the event of a primary data centre failure. | RPO: 1 minute | Measure data loss after simulated failure | Critical |
| NFR-AVL-007 | The mobile money payment flow SHALL retry failed transactions up to **2 times automatically** before reporting failure to the user (F-011). | 2 retries | Simulate M-Pesa API timeout; verify retry behaviour | Critical |
| NFR-AVL-008 | If a third-party dependency (M-Pesa API, SMS gateway, Maps API) is unavailable, the system SHALL degrade gracefully and continue serving other functions. The failure of one integration SHALL NOT cascade to unrelated system functions. | Graceful degradation | Simulate each third-party outage; verify other functions continue | High |

### 4.3 Offline Resilience

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-AVL-009 | The shopper app SHALL continue to display current order information from cached data for at least **15 minutes** after network loss (K-004). | 15 min offline | Enable airplane mode; verify order details accessible | High |
| NFR-AVL-010 | The shopper app SHALL queue GPS points locally and transmit them when connectivity resumes (K-004). | Queue and sync | Kill network; generate GPS data; restore network; verify data arrives server-side | High |
| NFR-AVL-011 | The customer app SHALL cache the current order status locally and display it when network is unavailable. | Cache order status | Kill network; verify customer can see current status | Medium |

---

## 5. Security Requirements

### 5.1 Encryption

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-SEC-001 | All API communications SHALL be encrypted using **TLS 1.2 or higher**. HTTP (unencrypted) SHALL NOT be accepted for any API endpoint. | TLS 1.2+ | SSL/TLS scan of all endpoints; reject protocols below 1.2 | Critical |
| NFR-SEC-002 | All passwords SHALL be stored using **bcrypt with cost factor 12** (or equivalent adaptive hashing algorithm). | bcrypt cost 12 | Password audit; verify no plaintext or weak hashing | Critical |
| NFR-SEC-003 | All personally identifiable information (PII) at rest SHALL be encrypted using **AES-256**. This includes: full name, phone number, government ID data, payment account numbers. | AES-256 at rest | Database encryption audit | Critical |
| NFR-SEC-004 | All encryption keys SHALL be managed using a hardware security module (HSM) or cloud key management service (AWS KMS / Azure Key Vault / GCP Cloud KMS). Keys SHALL NOT be stored in source code, configuration files, or environment variables. | HSM/KMS managed | Key management audit | Critical |

### 5.2 Authentication & Authorisation

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-SEC-005 | The system SHALL implement OTP-based two-factor authentication for: (a) account registration, (b) password/phone number change, (c) high-value order confirmation (> 200,000 TZS). | 2FA for critical actions | Verify OTP required for each listed action | Critical |
| NFR-SEC-006 | The system SHALL implement rate limiting: max **3 OTP requests per phone number per hour**, max **5 failed login attempts per account per 15 minutes**. | Rate limited | Automated test: exceed thresholds; verify temporary block | Critical |
| NFR-SEC-007 | The system SHALL enforce session timeout after **30 minutes of inactivity** for all user-facing apps. | 30 min timeout | Leave app idle for 31 min; verify re-authentication required | High |
| NFR-SEC-008 | The admin dashboard SHALL enforce a **15-minute inactivity timeout** due to the elevated privilege level. | 15 min admin timeout | Leave admin dashboard idle for 16 min; verify re-auth required | High |
| NFR-SEC-009 | The system SHALL implement device binding: a session SHALL be tied to the registering device. Login from a new device SHALL require OTP verification (I-008). | Device binding | Attempt login from new device; verify OTP requirement | Critical |
| NFR-SEC-010 | The system SHALL support up to **3 authorised devices per user account** (I-008). | 3 devices max | Register 4 devices; verify 4th is rejected | High |

### 5.3 Data Protection

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-SEC-011 | The system SHALL NOT transmit full mobile money account numbers in API responses or logs. Mobile money numbers SHALL be masked (e.g., 255***1234) or tokenised. | Masked identifiers | Audit API responses and logs for unmasked numbers | Critical |
| NFR-SEC-012 | The system SHALL log all administrative actions with: admin ID, timestamp, action type, affected entity, and previous/new values (L-002). | Full admin audit trail | Verify log entries for every admin action type | Critical |
| NFR-SEC-013 | Audit logs SHALL be append-only (immutable). Corrections SHALL be made by adding a new entry, not modifying or deleting an existing one (L-002). | Append-only | Attempt to modify or delete a log entry; verify rejection | Critical |
| NFR-SEC-014 | The system SHALL implement role-based access control (RBAC) for the admin dashboard with at least 3 tiers: Super Admin (full access, max 2 people), Operations Admin (shopper/order management), Support Agent (dispute handling, read-only) (L-001). | 3 admin roles | Verify each role has correct permissions | Critical |

### 5.4 Fraud Prevention Integration

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-SEC-015 | The fraud detection system SHALL process transaction velocity checks within **100ms** of order submission to enable real-time blocking. | < 100ms | Load test velocity check endpoint | High |
| NFR-SEC-016 | The GPS spoofing detection system SHALL analyse incoming GPS pings for spoofing patterns within **50ms** per ping (C-010). | < 50ms/ping | Measure spoofing check processing time per ping | High |

---

## 6. Data Requirements

### 6.1 Data Retention

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-DATA-001 | Customer personal data SHALL be retained for the duration of the account's active status plus **3 years** after deactivation (A-009). | Active + 3 years | Verify data deletion scripts and retention configuration | High |
| NFR-DATA-002 | Transactional data (orders, payments) SHALL be retained for **7 years** for tax and legal compliance (A-009, L-003). | 7 years | Verify archival strategy and retrieval capability | Critical |
| NFR-DATA-003 | Chat logs SHALL be retained for **90 days** from order completion, or until dispute resolution if longer (I-010). | 90 days | Verify chat log expiry and deletion process | High |
| NFR-DATA-004 | GPS tracking data SHALL be retained for **30 days** (L-003). | 30 days | Verify GPS data retention configuration | Medium |
| NFR-DATA-005 | Completed orders SHALL be archived (moved from active tables to cold storage) **90 days** after "Completed" status (D-013). | 90 days to archive | Verify archival job and data accessibility after archival | Medium |
| NFR-DATA-006 | Archived records SHALL be retained for at least **1 year**, or longer if required by applicable legal, tax, or accounting obligations (D-013). | 1 year min retention | Verify archival storage retention policy | Medium |

### 6.2 Data Backup

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-DATA-007 | The database SHALL be backed up: hourly incremental + daily full backup (L-009). | Hourly + daily | Verify backup job execution and success logs | Critical |
| NFR-DATA-008 | Media files (receipt photos, profile images) SHALL be backed up daily (L-009). | Daily media backup | Verify media backup job | High |
| NFR-DATA-009 | All backups SHALL be stored in a **geographically separate location** from the primary infrastructure (L-009). | Geo-redundant storage | Verify backup storage region/availability zone | Critical |
| NFR-DATA-010 | Backup retention: daily backups retained for **30 days**, weekly for **6 months**, monthly for **2 years** (L-009). | Tiered retention | Verify backup lifecycle policy | High |
| NFR-DATA-011 | Backup restore SHALL be tested at least **quarterly** with a documented restore drill. | Quarterly restore test | Verify restore test reports | Medium |

---

## 7. Usability & Accessibility Requirements

### 7.1 Mobile App Usability

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-UX-001 | The customer app SHALL support completion of the entire order placement flow (item entry → submission) in no more than **6 screens/taps** for a 5-item order. | ≤ 6 screens | UX audit; measure screens to completion with 10 users | High |
| NFR-UX-002 | The shopper app SHALL display the 30-second offer timer with: (a) prominent countdown visual, (b) vibration alert on offer receipt, (c) audible alert (configurable) (C-003). | Visible + vibration + audio | Verify all three alert mechanisms function | Critical |
| NFR-UX-003 | The total app installation size (APK) SHALL not exceed **25 MB** to accommodate users with limited storage and data budgets. | ≤ 25 MB APK | Measure APK size; remove unnecessary assets | High |
| NFR-UX-004 | The app SHALL function acceptably on devices with **1 GB RAM** running **Android 8.0 (API level 26)** or higher. | 1 GB RAM, API 26+ | Test on reference device (Moto E5 or equivalent) | Critical |
| NFR-UX-005 | The app SHALL use no more than **5 MB of mobile data per 10-minute session** of normal usage (tracking, status refreshes, chat). | ≤ 5 MB / 10 min | Profile app data usage over standard session | High |
| NFR-UX-006 | The app SHALL display a loading indicator for any operation taking longer than **2 seconds**. | Loading indicator at 2s | Measure and verify all operations > 2s show indicator | Medium |
| NFR-UX-007 | All error messages SHALL be displayed in the user's selected language (Swahili or English) and SHALL be human-readable, non-technical, and actionable. | Localised, actionable errors | Review all error message strings | High |
| NFR-UX-008 | The app SHALL support a **"data saver" mode** that reduces image quality, disables auto-download, and minimises background data usage. Toggle available in settings. | Data saver mode | Verify reduced data usage when mode is enabled | Medium |

### 7.2 Accessibility

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-UX-009 | The app SHALL support a minimum **touch target size of 48x48dp** for all interactive elements (buttons, links, form fields). | 48x48dp minimum | UI audit of all interactive elements | Medium |
| NFR-UX-010 | The app SHALL support font size scaling to at least **1.5x** the default size without breaking layouts. | 1.5x font scaling | Test with system font size set to largest setting | Medium |
| NFR-UX-011 | The app SHALL NOT rely solely on colour to convey information (e.g., status indicators SHALL include text labels or icons). | Colour-independent | Audit for colour-only status indicators | Medium |

---

## 8. Localisation & Internationalisation Requirements

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-L10N-001 | The customer and shopper apps SHALL support **Swahili and English** at launch. The active language SHALL default to the device language setting and be changeable in-app (A-007). | Swahili + English | Switch device language; verify app text switches accordingly | High |
| NFR-L10N-002 | All user-facing text SHALL be externalised to locale-specific resource files. No hard-coded user-facing strings in application code. | Externalised strings | Code review: verify no hard-coded UI strings | Critical |
| NFR-L10N-003 | The system architecture SHALL support adding new languages (French, Portuguese for regional expansion) without code changes — only resource file additions and configuration updates. | Extensible language framework | Add a test language resource; verify it loads without code changes | High |
| NFR-L10N-004 | Date formats SHALL follow the Tanzanian convention: **DD/MM/YYYY**. Time SHALL be displayed in **East Africa Time (EAT, UTC+3)**. Currency SHALL be displayed in **TZS** with the format "TZS X,XXX". | TZ-localised formats | Verify date/time/currency display across the app | High |
| NFR-L10N-005 | The system SHALL support **right-to-left (RTL) layout** preparation in the UI framework for future Arabic-language localisation (if expanding to Zanzibar or neighbouring regions). | RTL-ready framework | Verify UI framework supports RTL (implementation deferred) | Low |
| NFR-L10N-006 | The SMS notification system SHALL detect the user's language preference and send SMS messages in the appropriate language (Swahili or English). | Language-aware SMS | Verify SMS language matches user preference | High |

---

## 9. Compliance Requirements

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-COMP-001 | The platform SHALL comply with **Tanzania Communications Regulatory Authority (TCRA)** requirements for electronic communications and data services. | TCRA compliance | Legal review; maintain required registrations | Critical |
| NFR-COMP-002 | The platform SHALL comply with **Tanzania Revenue Authority (TRA)** requirements for tax reporting, including: (a) monthly transaction summaries, (b) quarterly tax submissions, (c) annual financial statements (L-004). | TRA compliance | Verify report generation; legal review | Critical |
| NFR-COMP-003 | The platform SHALL comply with **Bank of Tanzania (BOT)** regulations for electronic payments and mobile money transactions. This includes transaction reporting and anti-money laundering (AML) obligations. | BOT compliance | Legal review; verify transaction reporting capability | Critical |
| NFR-COMP-004 | The platform SHALL implement **anti-money laundering (AML)** detection as specified in I-002 (velocity checks) and I-011 (mule account detection). | AML detection | Verify detection rules; document reporting process | High |
| NFR-COMP-005 | The platform SHALL comply with emerging **data protection and privacy laws** in Tanzania. This includes: data subject rights (access, rectification, erasure), consent management, data breach notification, and cross-border data transfer restrictions. | Data protection compliance | Legal review; implement data subject request workflow | High |
| NFR-COMP-006 | The platform SHALL retain all financial transaction records for **7 years** as required by Tanzanian tax law (A-009, L-003). | 7-year retention | Verify archival and retrieval systems | Critical |
| NFR-COMP-007 | The platform SHALL implement a **terms of service acceptance** mechanism where: (a) current ToS is accepted at registration, (b) updated ToS requires re-acceptance at next login, (c) major changes (fees, data usage, liability, rights) require OTP-verified express consent (L-007). | ToS acceptance | Verify all three acceptance mechanisms | Critical |
| NFR-COMP-008 | The platform SHALL handle **prohibited items** as defined in I-007 and SHALL refuse orders containing: illegal drugs, weapons, alcohol (pending licensing), flammable materials, live animals. | Prohibited item blocking | Automated test: attempt to order each prohibited category | Critical |

---

## 10. Disaster Recovery & Business Continuity

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-DR-001 | The platform SHALL maintain a documented **Disaster Recovery Plan (DRP)** that is reviewed and tested at least **annually**. | Annual DRP review + test | Verify DRP document exists; review test results | High |
| NFR-DR-002 | The system SHALL be deployed across **at least two availability zones** within the primary cloud region. | Multi-AZ deployment | Verify infrastructure configuration | Critical |
| NFR-DR-003 | In the event of a full region failure, the system SHALL have the capability to be restored in a secondary region within **4 hours**, using data backups stored in a separate geographic location (RTO: 4 hours for full recovery). | RTO: 4 hours (full recovery) | Document recovery runbook; test at least annually | High |
| NFR-DR-004 | The system SHALL have a documented **communication plan** for notifying customers, shoppers, and stakeholders during major incidents. The plan SHALL include: primary contact channels, escalation tree, SLA for initial communication (< 30 minutes of incident declaration). | Incident communication plan | Verify plan document; test in annual DR drill | High |
| NFR-DR-005 | Mission-critical dependencies (payment provider APIs) SHALL have at least **two provider options** (e.g., M-Pesa + Mixx) so that if one provider experiences a prolonged outage, the platform can switch to the alternative with configuration changes only (no code changes). | Multi-provider fallback | Verify provider abstraction layer; test switch | Critical |

---

## 11. Monitoring, Logging & Auditability

### 11.1 Application Monitoring

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-MON-001 | The system SHALL monitor and report on all key SLAs: order acceptance time (< 30s), shopping time (dynamic tiered), delivery time (dynamic ETA), support response time (< 5 min urgent, < 2 hr standard), dispute resolution time (< 24 hr) (L-005). | Full SLA monitoring | Verify SLA metrics are collected, displayed, and alerted | Critical |
| NFR-MON-002 | SLA breaches SHALL trigger automated alerts to the Operations Admin. Persistent breaches (> 10% above target for 7 days) SHALL trigger a formal review (L-005). | Automated alerts on breach | Verify alert configuration; test breach scenario | High |
| NFR-MON-003 | The system SHALL expose health check endpoints for all services (API, database, cache, queue, external integrations) that can be used by load balancers and monitoring systems for automated health checks. | Health check endpoints | Verify /health returns correct status for each service | Critical |
| NFR-MON-004 | The system SHALL track **application performance metrics** (APM) including: request latency, error rates, throughput, and resource utilisation (CPU, memory, disk, network) with **1-minute granularity**. | 1-min APM granularity | Verify APM tool configuration (Datadog, New Relic, CloudWatch, etc.) | High |
| NFR-MON-005 | The system SHALL track **business metrics** in real-time and display on the admin dashboard (L-010): (a) live orders map, (b) daily/weekly/monthly GMV, (c) active shopper count, (d) order fulfillment rate, (e) average assignment time, (f) dispute rate, (g) new user registrations. | Live business metrics | Verify metrics are collected, displayed, and refresh at correct intervals | Critical |

### 11.2 Logging

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-LOG-001 | All financial transactions SHALL be logged with: timestamp, amount, type, user ID, order ID, payment reference, and admin ID (if manual). Logs SHALL be immutable (append-only) (L-002). | Immutable financial logs | Verify log structure; test immutability | Critical |
| NFR-LOG-002 | All order status transitions SHALL be logged with: timestamp, from-state, to-state, trigger event, actor, and reason (D-004). | Status transition log | Verify state machine logging | Critical |
| NFR-LOG-003 | All application logs SHALL be structured (JSON) with: timestamp, service name, log level (DEBUG/INFO/WARN/ERROR/FATAL), request ID, correlation ID, and message. | Structured JSON logging | Verify log format across all services | High |
| NFR-LOG-004 | Logs SHALL be centrally aggregated and searchable (e.g., ELK Stack, CloudWatch Logs, Datadog Logs) with a minimum retention of **90 days** for operational logs. | 90-day log retention | Verify centralised logging configuration | High |
| NFR-LOG-005 | Security-relevant events SHALL be logged separately (security audit log) with **1-year retention**. Events include: login failures, permission changes, account suspensions, data access anomalies. | Security audit log: 1 year | Verify security log configuration and retention | Critical |
| NFR-LOG-006 | The system SHALL NOT log sensitive data: passwords, OTP codes, full mobile money numbers, or chat message content in plain application logs. | No sensitive data in logs | Log audit: verify no sensitive data leakage | Critical |

### 11.3 Alerting

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-ALERT-001 | The system SHALL alert Operations Admin within **5 minutes** of: (a) any service being down or unhealthy, (b) SLA breach, (c) payment provider API failure, (d) fraud alert triggered (I-001—I-011). | < 5 min alert | Verify alert configuration for each scenario | Critical |
| NFR-ALERT-002 | Alert notifications SHALL be sent via at least **two channels** (e.g., in-app admin alert + SMS/email to on-call engineer). | Dual-channel alerting | Verify notification reaches at least two channels | High |

---

## 12. Maintainability Requirements

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-MAINT-001 | The system SHALL follow a **modular, layered architecture** with clearly separated layers: API gateway, application services, business logic, data access, external integrations. Dependencies SHALL flow inward only. | Layered architecture | Architecture review; verify dependency direction | Critical |
| NFR-MAINT-002 | The system SHALL expose a **versioned REST API** (e.g., /api/v1/) for all public endpoints. API versioning SHALL support backward compatibility for at least **two minor versions**. | Versioned API | Verify /api/v1/ and /api/v2/ can coexist | High |
| NFR-MAINT-003 | The codebase SHALL maintain **test coverage** of at least: (a) **80%** for business logic and service layers, (b) **70%** for UI components, (c) **90%** for critical paths (payment, assignment, state transitions). | 80% logic, 70% UI, 90% critical | Measure coverage in CI pipeline; enforce minimum thresholds | High |
| NFR-MAINT-004 | The system SHALL support **feature flags** to enable/disable features without code deployment. Feature flag configuration SHALL be changeable at runtime without requiring a service restart. | Runtime feature flags | Toggle a feature flag; verify feature activates without restart | High |
| NFR-MAINT-005 | All third-party API integrations (M-Pesa, Mixx, SMS, push notifications, Maps) SHALL be behind an **abstraction layer** that allows swapping providers with configuration changes only — no code changes required. | Provider abstraction | Simulate M-Pesa provider swap via config; verify no code changes | Critical |
| NFR-MAINT-006 | The system SHALL have a **CI/CD pipeline** with automated: build, lint, test, security scan, and deploy stages. Deployments to production SHALL require manual approval. | CI/CD pipeline | Verify pipeline configuration; test deployment flow | High |
| NFR-MAINT-007 | The system SHALL support **zero-downtime deployments** (rolling update or blue/green) to avoid interrupting active orders during updates. | Zero-downtime deploys | Verify deployment strategy; test with active traffic | High |
| NFR-MAINT-008 | A new developer SHALL be able to set up a local development environment and run the full test suite in under **30 minutes**, with documented setup instructions. | < 30 min setup | Test with new developer; document steps | Medium |

---

## 13. Interoperability Requirements

| ID | Requirement | Target | Verification | Priority |
|----|-------------|--------|--------------|----------|
| NFR-INT-001 | The system SHALL integrate with **M-Pesa API** for: (a) customer payment pre-authorisation and capture, (b) shopper payout processing, (c) refund processing. API version and integration documented (F-003, F-006). | M-Pesa integration | Verify all three payment flows | Critical |
| NFR-INT-002 | The system SHALL integrate with **Mixx API** for the same payment operations as M-Pesa, providing payment provider redundancy (F-003, F-006). | Mixx integration | Verify all three payment flows | Critical |
| NFR-INT-003 | The system SHALL integrate with **Airtel Money API** for payment operations (V1) (F-003, F-006). | Airtel Money integration | Verify payment flows | High |
| NFR-INT-004 | The system SHALL integrate with an **SMS gateway provider** (Africastalking or Twilio) for: (a) OTP delivery, (b) order notifications, (c) emergency communications (J-003, J-008). | SMS gateway | Verify all three SMS use cases | Critical |
| NFR-INT-005 | The system SHALL integrate with **Firebase Cloud Messaging (FCM)** or equivalent for push notification delivery to Android devices (J-003). | Push notifications | Verify push delivery to test devices | Critical |
| NFR-INT-006 | The system SHALL integrate with a **Maps service** (Google Maps Platform or OpenStreetMap) for: (a) road distance calculation (C-002), (b) geocoding, (c) route estimation (D-005, E-003). | Maps integration | Verify distance calculation accuracy | Critical |
| NFR-INT-007 | The system SHALL integrate with **NIDA** (National Identification Authority) APIs when they become accessible for identity verification (B-003). | NIDA API integration (as available) | Verify identity verification flow with NIDA | Medium |
| NFR-INT-008 | All integrations SHALL have documented **SLA expectations** and **fallback behaviours**. If an integration is unavailable, the system SHALL degrade gracefully (NFR-AVL-008). | Documented SLA + fallback | Verify documentation exists for each integration; test fallback | High |

---

## 14. Traceability Matrix

| NFR ID | Category | Source | Business Rules | Priority |
|--------|----------|--------|----------------|----------|
| NFR-PERF-001 through -013 | Performance | C-003, C-004, D-005, E-003, F-003, F-011 | C-003, C-004, D-005, E-003, F-003, F-011 | Critical/High |
| NFR-SCAL-001 through -008 | Scalability | — | — | Critical/High |
| NFR-AVL-001 through -011 | Availability | K-004, L-006, F-011 | K-004, L-006, F-011 | Critical/High/Medium |
| NFR-SEC-001 through -016 | Security | I-008, L-001, L-002, I-010 | I-008, L-001, L-002, I-010 | Critical/High |
| NFR-DATA-001 through -011 | Data | A-009, D-013, I-010, L-003, L-009 | A-009, D-013, I-010, L-003, L-009 | Critical/High/Medium |
| NFR-UX-001 through -011 | Usability | C-003, A-007 | C-003, A-007 | Critical/High/Medium |
| NFR-L10N-001 through -006 | Localisation | A-007 | A-007 | High/Low |
| NFR-COMP-001 through -008 | Compliance | L-004, L-007, I-007, A-009, L-003 | L-004, L-007, I-007, A-009, L-003 | Critical/High |
| NFR-DR-001 through -005 | Disaster Recovery | L-009, L-006 | L-009, L-006 | Critical/High |
| NFR-MON-001 through -005, NFR-LOG-001 through -006, NFR-ALERT-001 through -002 | Monitoring/Logging | L-002, L-005, L-010, D-004 | L-002, L-005, L-010, D-004 | Critical/High |
| NFR-MAINT-001 through -008 | Maintainability | — | — | Critical/High/Medium |
| NFR-INT-001 through -008 | Interoperability | C-002, F-003, F-006, J-003, B-003 | C-002, F-003, F-006, J-003, B-003 | Critical/High/Medium |

---

*This document is Phase 6 of the Urban Shopper Platform specification. It feeds into Phase 7 (System Architecture) and Phase 8 (IEEE 29148 SRS).*
