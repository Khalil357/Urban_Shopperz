# Urban Shopper Platform — Release Scope Definition

> **Document Type:** Business Analysis  
> **Status:** Complete  
> **Date:** 2026-07-24  
> **Version:** 1.0  
> **Cross-Reference:** Vision & Product Definition — `02-vision-product-definition.md`, Business Rules — `03-business-rules.md`, Workflows — `04-business-workflows.md`

---

## Table of Contents

1. [Scope Philosophy](#1-scope-philosophy)
2. [Scope Summary Table](#2-scope-summary-table)
3. [MVP (Launch) — Detailed Scope](#3-mvp-launch--detailed-scope)
4. [Version 1.0 — Detailed Scope](#4-version-10--detailed-scope)
5. [Version 2.0 — Detailed Scope](#5-version-20--detailed-scope)
6. [Future Vision — Beyond V2](#6-future-vision--beyond-v2)
7. [Scope Decision Log](#7-scope-decision-log)

---

## 1. Scope Philosophy

### MVP (Month 0-6)
**"Make it work for one city, one neighbourhood at a time."**
Only the minimum features needed to prove the core marketplace loop: Customer submits request → Shopper accepts via GPS → Shops → Delivers → Gets paid → Gets rated. Everything else is cut. The MVP is not "minimal" in the sense of incomplete — it is minimal in the sense of *focused*.

### Version 1.0 (Month 6-9)
**"Make it reliable and scalable."**
Add the features that reduce operational burden, improve trust, enable better fraud detection, and support multi-language reach. The platform should require minimal manual intervention.

### Version 2.0 (Month 9-18)
**"Make it defensible and diversified."**
Add vendor integration, corporate accounts, loyalty, AI features — the moat-builders. These features require order volume data to be effective, so they come after the platform has traction.

### Future Vision (18+ months)
**"Expand horizontally and vertically."**
New geographies (East Africa), new verticals (fintech, logistics), new delivery models (batch, autonomous). Only once the core business is proven and profitable.

---

## 2. Scope Summary Table

| Feature Area | MVP | V1.0 | V2.0 | Future |
|-------------|-----|------|------|--------|
| **Customer App** | Android app, registration, phone verification, item entry, order tracking (status-based), in-app chat, rating | iOS app, map-based order tracking, USSD/PWA fallback, saved lists, multi-language | Barcode scanning, voice entry, AI recommendations | Augmented reality item preview |
| **Shopper App** | Android app, registration, ID upload, GPS ping, 30-sec offer, manual item status, receipt photo upload | iOS app, performance dashboard, earnings analytics, shift scheduling | Route optimization, batch delivery mode | Voice navigation, AI shopping assistant |
| **Assignment Engine** | Nearest-available, Haversine distance, 30-sec timeout, offer cascade, 10-min arrival SLA | Dynamic radius, zone management dashboard, fairness distribution | ML-based assignment, simultaneous offers for low supply | Fully autonomous supply-demand balancing |
| **Payments** | M-Pesa/Mixx integration, pre-auth holds, wallet (customer + shopper), 24-hr shopper settlement, basic fee calculation | Escrow integration, promo engine, advanced reconciliation | Corporate invoicing NET-30, multi-currency | Fintech products (loans, insurance) |
| **Customer Support** | In-app dispute form, email support, manual resolution | Support ticket system, canned responses, SLA tracking | AI-powered dispute resolution, chatbot | Self-service dispute portal |
| **Fraud Detection** | Basic rule-based (duplicate accounts, velocity, prohibited items) | Advanced rules (receipt verification, GPS spoofing detection) | ML-based fraud detection, real-time blocking | Predictive fraud prevention |
| **Admin Dashboard** | Basic: orders, shoppers, disputes, metrics | Enhanced: reports, analytics, zone management, fraud dashboard | Predictive analytics, automated interventions | Full business intelligence suite |
| **Vendor Integration** | Out of scope | Out of scope | Vendor portal, inventory API, pricing tools | Dynamic pricing, demand forecasting |
| **Corporate Accounts** | Out of scope | Out of scope | Corporate registration, invoicing, bulk orders, admin controls | Procurement API integration |
| **Internationalisation** | Swahili + English language | Full i18n framework | Currency localisation | Multi-country deployment |
| **Geographic Coverage** | Dar es Salaam (selected zones) | Dar es Salaam (all zones) | Arusha, Mwanza | East Africa (Kenya, Uganda, Rwanda) |

---

## 3. MVP (Launch) — Detailed Scope

### Geographic Scope
- **City:** Dar es Salaam only
- **Zones (launch order):** Masaki, Oyster Bay, Mikocheni, City Centre, Kariakoo, Mbezi Beach, Tabata, Kimara
- **Launch criteria per zone:** Minimum 20 active shoppers registered within the zone before accepting customer orders

### Platform Infrastructure

| Component | Scope | Notes |
|-----------|-------|-------|
| Backend API | Core REST API for all MVP features | Must be designed for horizontal scaling |
| Database | Relational (PostgreSQL) + Redis for real-time data | MVP does not require document store |
| Hosting | Cloud hosting (AWS/Azure/Google Cloud) in South Africa or Europe with East Africa CDN | Latency to Dar es Salaam is critical |
| SMS Gateway | Twilio or Africastalking integration | OTP + order notifications |
| Push Notifications | Firebase Cloud Messaging (Android) | — |
| Mobile Money API | M-Pesa (Vodacom) + Mixx (Yas) integration at launch. Airtel Money in V1. | Direct API integration (no third-party payment gateway dependency) |

### Customer App Features (Android MVP)

| Feature | Detail | Ref |
|---------|--------|-----|
| Registration | Phone number + OTP verification, name, language preference | A-001, A-003, A-007 |
| Profile Management | View/edit name, phone, language, notification prefs | A-001, A-007 |
| Order Creation | Manual item entry with predictive text, quantity, notes per item | D-002 |
| Delivery Address | Pin-drop on map + manual text entry | E-005 |
| Price Estimation | Item estimate + 15% service fee + distance-based delivery fee | D-003, F-001, F-002 |
| Payment Method | Wallet (pre-funded via M-Pesa/Mixx) or Cash on Delivery | F-003, F-012 |
| Substitution Preferences | Per-item: Best Match / Contact Me / No Substitutions | D-007 |
| Order Tracking | Status-based tracking with status bar (no live map) | D-004 |
| Push Notifications | Order status changes (Accepted, Shopping, Delivered, etc.) | J-004 |
| In-App Chat | Text-only chat with assigned shopper during active order | J-001 |
| Order History | List of past orders with status, cost, shopper name | A-010 |
| Rating & Feedback | Rate shopper 1-5 stars with optional written feedback | H-001 |
| Wallet | View balance, deposit via M-Pesa/Mixx, view transaction history | F-009 |

### Shopper App Features (Android MVP)

| Feature | Detail | Ref |
|---------|--------|-----|
| Registration | Phone OTP, ID upload (photo), selfie, transport docs, background check consent | B-001, B-002, B-003, B-009 |
| Go Online/Offline | Toggle availability; auto-offline after 15 min no GPS | B-005 |
| Order Offers | Push notification + in-app alert with 30-second countdown | C-003, J-003 |
| Offer Details | Item count, market location, distance, estimated pay | — |
| Order Management | Accept/Decline; current order screen with progress steps | — |
| Item Management | Mark items as Found/Substituted/Not Available | D-006 |
| Substitution Handling | Chat customer (Contact Me) or auto-select (Best Match/No Subs) | D-007 |
| Receipt Upload | Photo capture + upload, manual price entry fallback | D-008 |
| Navigation | Opens default maps app with directions to market/delivery location | — |
| GPS Tracking | Background location every 10 seconds when online/on-order | C-001 |
| In-App Chat | Text-only chat with customer during active order | J-001 |
| Earnings Screen | Current order earnings, pending balance, available balance | F-010 |
| Withdrawal | Transfer available balance to M-Pesa/Mixx | F-010 |
| Customer Rating | Rate customer 1-5 stars after delivery | H-002 |
| Profile | View rating, earnings history, order history | — |

### Assignment Engine (MVP)

| Feature | Detail | Ref |
|---------|--------|-----|
| Nearest-Shopper Matching | Haversine distance calculation from shopper GPS to market | C-002 |
| 30-Second Acceptance Window | Strict countdown from notification delivery | C-003 |
| Declined/Timeout Cascade | Immediately offer to next nearest shopper | C-004 |
| Max Cascade Duration | 5 minutes of cycling through eligible shoppers | C-004 |
| 10-Min Arrival SLA | Shopper must mark "Arrived at Market" within 10 min or flag delayed | D-005 |
| Single-Order Assignment | One order per shopper at a time | B-010 |
| Zone-Based Coverage | Predefined neighbourhood zones with operating status | C-007 |

### Payments (MVP)

| Feature | Detail | Ref |
|---------|--------|-----|
| Mobile Money Integration | M-Pesa and Mixx API for deposits, holds, payouts, withdrawals | F-003, F-006 |
| Customer Wallet | In-app wallet for deposits, fee payments, refunds | F-009 |
| Shopper Wallet | Pending balance + available balance, withdrawals | F-010 |
| Pre-Authorisation Hold | Hold estimated total on customer wallet at order submission | F-003 |
| Final Payment | Charge actual total after receipt upload, refund difference | F-004 |
| Shopper Settlement | 24-hour settlement from "Delivered" status | F-006 |
| First-Order Promotion | 50% off delivery fee (capped 3,000 TZS) | F-007 |
| Cash on Delivery | Available for eligible customers (5+ orders, no disputes) | F-012 |

### Admin Dashboard (MVP)

| Feature | Detail | Ref |
|---------|--------|-----|
| Shopper Applications | Review pending applications, approve/reject | B-002, 3.1 |
| Order Management | View all orders, search, filter by status | — |
| Manual Cancellation | Admin-initiated cancellation with reason | G-005 |
| Dispute Queue | View and resolve incoming disputes | 3.2 |
| Fraud Alerts | View flagged accounts and suspicious activity | 3.3 |
| Basic Metrics | Daily GMV, order count, active shoppers, fulfillment rate | L-010 |
| User Lookup | Search customers/shoppers by ID, phone, name | — |
| Audit Log | View financial transaction log | L-002 |

### Customer Support (MVP)

| Feature | Detail | Ref |
|---------|--------|-----|
| In-App Issue Reporting | Customer can report order issues (missing/wrong/damaged) | 1.6 |
| Email Support | Support email with ticket tracking | — |
| Manual Resolution | Admin reviews evidence and processes refunds | 3.2 |

---

## 4. Version 1.0 — Detailed Scope

> **Timeline:** Month 6-9 post-MVP launch  
> **Objective:** Reliability, fraud prevention, scalability, operational efficiency

### New Features

| Feature | Detail | Priority | Ref |
|---------|--------|----------|-----|
| **iOS Customer App** | Native iOS app with full MVP feature parity | High | — |
| **iOS Shopper App** | Native iOS app with full MVP feature parity | High | — |
| **Live Map Tracking** | Real-time shopper location on map during delivery phase | High | C-001 |
| **Escrow Integration** | Integrate escrow service or self-managed trust account for payments | High | M-010 |
| **Airtel Money Integration** | Add Airtel Money as a third mobile money payment option | High | — |
| **Promotions Engine** | Admin-configurable promo codes (percentage, fixed amount, free delivery) | High | — |
| **Multi-Language Support** | Full Swahili UI + English; i18n framework for future languages | High | A-007 |
| **Enhanced Fraud Detection** | GPS spoofing detection, receipt price verification (OCR + market DB), mule detection | High | I-004, I-010, I-011 |
| **Scheduled Orders** | Order scheduling with release 30 min before delivery window | High | D-014 |
| **Shopper Performance Tiers** | Bronze/Silver/Gold/Platinum with differentiated commission rates and order priority | Medium | B-008 |
| **Support Ticket System** | In-app support ticket with status tracking, canned responses | Medium | — |
| **Advanced Admin Dashboard** | Zone management UI, SLA monitoring, fraud dashboard, enhanced reporting | Medium | L-005, L-010 |
| **PWA/USSD Fallback** | Progressive web app for feature phone customers — basic ordering and tracking | Medium | J-008 |
| **USSD Order Status** | Feature phone users can dial USSD code to check order status | Low | J-008 |
| **Saved Item Lists** | Customers can save and re-order from previous orders or create reusable lists | Low | — |
| **Ratings Breakdown** | Display ratings broken down by criteria (accuracy, quality, timeliness, communication) | Low | H-001 |
| **Referral Programme** | Customer referral with credits for both referrer and referee | Medium | F-008 |
| **Performance Optimisation** | Database indexing, query optimisation, caching layer | High | — |
| **Automated Data Archival** | 90-day archival of completed orders, automated purge scheduling | Low | D-013, L-003 |

### Enhancements

| Enhancement | Detail | Ref |
|-------------|--------|-----|
| Assignment radius dynamic expansion | Smart radius based on real-time supply/density | C-006 |
| Fairness distribution algorithm | Ensure equitable order distribution across shoppers | C-008 |
| Bayesian rating adjustment | More accurate ratings for shoppers with few reviews | H-003 |
| Support SLA monitoring | Auto-escalation of support tickets breaching response SLAs | L-005 |
| Receipt OCR | Automated reading of receipt photos for price verification | D-008 |

---

## 5. Version 2.0 — Detailed Scope

> **Timeline:** Month 9-18 post-MVP launch  
> **Objective:** Defensibility, diversification, monetisation expansion

### Geographic Expansion

| City | Country | Rationale |
|------|---------|-----------|
| Arusha | Tanzania | Tourist hub, growing middle class, tech ecosystem |
| Mwanza | Tanzania | Lakeside city, significant population, underserved market |

### New Features

| Feature | Detail | Ref |
|---------|--------|-----|
| **Vendor Integration Portal** | Registered vendors list inventory, set prices, receive digital orders | M-004 |
| **Vendor Inventory API** | REST API for vendors to sync inventory programmatically | M-004 |
| **Multi-Shop Orders** | Customer orders from 2+ vendors in one request; shopper visits all locations | M-001, M-002 |
| **Corporate Accounts** | Business registration, NET-30 invoicing, employee sub-accounts, spending limits | M-003 |
| **Loyalty Programme** | Points per TZS spent, tiered membership, delivery fee discounts | M-011 |
| **Recommendation Engine** | "Frequently bought together" and "Based on your history" suggestions | M-012 |
| **Route Optimization** | Optimal path calculation with traffic awareness for shoppers | M-006 |
| **Batch Delivery** | Experienced shoppers may take 2-3 orders on the same route (customer opt-in) | E-006 |
| **AI Demand Prediction** | Predict order volume by zone; recommend shopper shifts | M-005 |
| **Customer Chat Translation** | Real-time Swahili ↔ English translation in chat | J-006 |
| **Content Moderation** | ML-based detection of prohibited content in chat | J-007 |
| **Dynamic Delivery Fee** | Real-time pricing based on demand, weather, traffic | F-002 |
| **Advanced Reporting** | Custom report builder, automated email reports, export API | L-004 |
| **Automated Quality Audits** | Auto-audit of order quality metrics with alerting | H-007 |

---

## 6. Future Vision — Beyond V2

> **Timeline:** 18+ months  
> **Objective:** Regional expansion and new verticals

### Geographic

| Market | Rationale |
|--------|-----------|
| Kenya (Nairobi, Mombasa) | Largest East African economy, similar mobile money ecosystem (M-Pesa dominant) |
| Uganda (Kampala) | Growing urban population, low delivery competition |
| Rwanda (Kigali) | Government digital-friendly, high mobile money usage |

### New Verticals

| Vertical | Description | Prerequisites |
|----------|-------------|---------------|
| **Shopper Fintech** | Micro-loans based on earnings history, insurance products | Regulatory approval, 2+ years of earnings data |
| **Logistics-as-a-Service** | B2B delivery services for local businesses | Established shopper network in target cities |
| **Freight** | Larger-item delivery (furniture, bulk goods) | Different vehicle types, insurance |
| **Q-Commerce (Dark Stores)** | Under-15-minute delivery of convenience items from platform-managed micro-fulfillment | High order density, real estate for dark stores |
| **Business Intelligence** | Sell anonymised market data insights to vendors and FMCG companies | Large order volume, data anonymisation framework |

### Technology Frontiers

| Technology | Application | Threshold |
|-----------|-------------|-----------|
| AI Voice Ordering | Customers dictate their shopping list | High-accuracy Swahili speech recognition |
| Computer Vision Receipt Verification | Automated item-by-item receipt matching | Training data from 100K+ receipts |
| Predictive Supply Management | Pre-position shoppers in anticipated high-demand zones | 6+ months of zone-level demand data |
| Autonomous/Drones | Last-mile delivery for select items | Regulatory approval, infrastructure readiness |

---

## 7. Scope Decision Log

This log records key scope decisions made during business analysis, including what was explicitly excluded and why.

| ID | Decision | Rationale | Made By | Date |
|----|----------|-----------|---------|------|
| SD-001 | MVP is Android-only. iOS in V1. | 90%+ smartphone market share in Tanzania is Android. iOS development cost not justified for MVP. | BA Team | 2026-07-24 |
| SD-002 | MVP uses status-based tracking, not live map. | Map integration adds significant dev time. Status tracking meets MVP needs. Live map in V1. | BA Team | 2026-07-24 |
| SD-003 | No vendor/inventory integration in MVP or V1. | Vendors lack digital systems. Manual integration doesn't scale. Wait for vendor portal (V2). | BA Team | 2026-07-24 |
| SD-004 | Customer does NOT select a specific shopper. | Assignment is algorithmic. Cherry-picking creates fairness issues and reduces reliability. | BA Team | 2026-07-24 |
| SD-005 | Single-order per shopper (no batching in MVP). | Quality over throughput. Batch delivery adds significant complexity. Defer to V2. | BA Team | 2026-07-24 |
| SD-006 | Escrow deferred from MVP to V1. | MVP can use direct M-Pesa holds + payouts. Escrow adds legal/financial licensing complexity. V1 with proper escrow or trust account. | BA Team | 2026-07-24 |
| SD-007 | Cash on Delivery available at MVP launch for eligible customers. | Critical for adoption in Tanzanian market where cash preference is strong. Risk-managed via eligibility criteria. | BA Team | 2026-07-24 |
| SD-008 | No restaurant/food delivery in scope. | Food delivery is a different operational model (temperature, timing, restaurant relationships). Competes with Uber Eats. Focus on shopping. | BA Team | 2026-07-24 |
| SD-009 | Geographic expansion only after MVP stability. | No new cities until 3 months of stable operations, positive unit economics, and sufficient shopper density in Dar. | BA Team | 2026-07-24 |
| SD-010 | Shopper deposit (20,000 TZS) deferred to V1. | MVP will rely on ratings and verification for trust. Deposit may deter initial shopper signup. Revisit when fraud patterns emerge. | BA Team | 2026-07-24 |
| SD-011 | SMS notifications for feature phone users deferred to V1. | MVP targets smartphone users. SMS integration costs and scaling come in V1 with PWA/USSD. | BA Team | 2026-07-24 |
| SD-012 | Customer service in MVP is email + in-app form only. No live chat support. | Live support team does not scale pre-revenue. Automated + email handles MVP volume. | BA Team | 2026-07-24 |

---

*This document feeds into the Master SRS Prompt (`06-master-srs-prompt.md`).*
