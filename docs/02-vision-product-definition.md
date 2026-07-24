# Urban Shopper Platform — Software Vision & Product Definition (SVPD)

> **⚠️ DEPRECATED — Superseded by `01-business-vision-and-strategy.md`**  
> This document was an earlier draft and has been superseded by Phase 1. Refer to `01-business-vision-and-strategy.md` for the current Business Vision & Product Strategy. Retained for historical reference only.

> **Document Type:** Business Analysis  
> **Status:** Complete  
> **Date:** 2026-07-24  
> **Version:** 1.0 (Pre-SRS)  
> **Cross-Reference:** Market Research — `01-market-research.md`

---

## Table of Contents

1. [Vision Statement](#1-vision-statement)
2. [Mission Statement](#2-mission-statement)
3. [Problem Statement](#3-problem-statement)
4. [Opportunity Statement](#4-opportunity-statement)
5. [Product Goals](#5-product-goals)
6. [Business Objectives](#6-business-objectives)
7. [Stakeholders](#7-stakeholders)
8. [Target Users](#8-target-users)
9. [User Personas](#9-user-personas)
10. [Assumptions](#10-assumptions)
11. [Constraints](#11-constraints)
12. [Risks](#12-risks)
13. [Success Metrics](#13-success-metrics)
14. [Value Proposition](#14-value-proposition)
15. [Scope](#15-scope)
16. [Out of Scope](#16-out-of-scope)
17. [Product Principles](#17-product-principles)

---

## 1. Vision Statement

To become Africa's most trusted on-demand marketplace — where anyone, anywhere, can get anything from any market, delivered by a verified shopper, within hours.

---

## 2. Mission Statement

To connect urban consumers with trusted independent shoppers who can purchase and deliver items from any market or store, creating economic opportunity for shoppers while delivering convenience and reliability for customers.

---

## 3. Problem Statement

**For Customers:**
Urban residents in Tanzania spend significant time and money travelling to markets, navigating traffic, haggling with vendors, and carrying purchases home. Existing delivery options are limited — food-only platforms (Uber Eats), informal boda-boda riders (no reliability, no tracking, no accountability), or self-transport. There is no single platform where a customer can request items from any market, any store, or any vendor and have them shopped and delivered by a verified, tracked, and rated shopper.

**For Shoppers:**
Millions of capable individuals (boda-boda riders, students, part-time workers) lack a structured platform to monetise their time and mobility. They work informally — inconsistent income, no reputation system, no insurance, no growth path. Existing gig platforms either exclude them (food-only focus) or offer poor terms (low pay, opaque algorithms).

**For Markets and Small Vendors:**
Traditional markets in Tanzania have no digital storefront. They cannot offer delivery, accept digital payments, or reach customers beyond their immediate foot traffic. They are excluded from the e-commerce revolution.

**The Core Problem:**
There is no trusted, multi-category, on-demand shopping and delivery platform designed specifically for the Tanzanian urban context — one that bridges the gap between informal market commerce and digital convenience.

---

## 4. Opportunity Statement

Tanzania is at an inflection point:
- **63.2 million mobile money accounts** — the payment infrastructure exists and is ubiquitous
- **4G coverage at 88% and rising** — connectivity is sufficient for real-time apps
- **Smartphone penetration at 36% and growing** — the addressable market expands yearly
- **Fragmented competitive landscape** — no platform has achieved density dominance
- **Multi-category delivery is open territory** — food delivery exists; grocery, retail, pharmacy, and market shopping are largely untapped
- **Rapid urbanisation** — Dar es Salaam's ~7M population creates sufficient density for marketplace dynamics

The platform can be the first mover in a market that currently has no dominant player, in a category (any-store shopping) that existing competitors do not serve.

---

## 5. Product Goals

| # | Goal | Timeframe | Measure |
|---|------|-----------|---------|
| G1 | Launch MVP in Dar es Salaam with 500+ active shoppers and 10,000+ registered customers | Month 6 | Active user counts |
| G2 | Achieve < 30-minute average assignment-to-acceptance time | Month 6 | System metric |
| G3 | Maintain 95%+ order fulfillment rate (accepted orders that complete successfully) | Month 6 | Fulfillment rate |
| G4 | Expand to 3 Tanzanian cities (Dar es Salaam, Arusha, Mwanza) | Month 18 | Cities live |
| G5 | Process 100,000+ orders per month | Month 18 | Order volume |
| G6 | Build a self-sustaining marketplace with positive unit economics per order | Month 12 | Contribution margin |
| G7 | Achieve average customer rating of 4.3+ out of 5 across all fulfilled orders | Month 9 | Average rating |
| G8 | Keep shopper acceptance rate above 80% of offered orders | Month 6 | Acceptance rate |

---

## 6. Business Objectives

| # | Objective | Rationale |
|---|-----------|-----------|
| BO1 | Generate revenue through service fees on each completed order | Primary revenue stream |
| BO2 | Achieve gross merchandise value (GMV) of 5B TZS/month by Month 18 | Scale indicator |
| BO3 | Maintain customer acquisition cost (CAC) under 5,000 TZS per registered customer | Unit economics |
| BO4 | Achieve customer lifetime value (LTV) to CAC ratio of 3:1 by Month 12 | Business sustainability |
| BO5 | Build data moat of 100,000+ completed orders with full lifecycle data by Month 18 | Competitive defensibility |
| BO6 | Establish regulatory-compliant operations as a foundation for East African expansion | Scalability |
| BO7 | Reach operational breakeven on direct order costs by Month 18 | Path to profitability |

---

## 7. Stakeholders

| Stakeholder | Role | Key Interest | Influence |
|-------------|------|-------------|-----------|
| **Customers** | Request items, pay for service, receive delivery | Convenience, reliability, fair pricing, item quality | High — demand side of marketplace |
| **Shoppers** | Accept orders, shop items, deliver to customers | Fair pay, consistent work, safety, transparent rules | High — supply side of marketplace |
| **Platform (Urban Shopper)** | Operates the marketplace, sets rules, handles payments, resolves disputes | Revenue, growth, brand reputation, regulatory compliance | Highest — owns the platform |
| **Markets & Vendors** (Future: V2) | Sell items that shoppers purchase | Increased sales, reliable payment, no operational burden | Medium — supply enabler |
| **Mobile Money Providers** (M-Pesa, Mixx, Airtel Money) | Process payments | Transaction volume, API reliability, SLA compliance | Medium — payment infrastructure |
| **Telecommunications Operators** (Vodacom, Airtel, Tigo/Yas, TTCL) | Provide network connectivity | Data traffic, subscriber services | Low-Medium — infrastructure |
| **Regulators** (TCRA, TRA, BOT) | Oversee telecommunications, tax, and financial services | Compliance, consumer protection, tax collection | High — can permit or block operations |
| **Investors** | Provide capital | ROI, growth metrics, market share, exit potential | High — funding source |
| **Insurance Partners** (Future) | Provide coverage for shoppers and orders | Premium volume, risk management | Low-Medium — enabler |
| **Corporate Clients** (Future: V2) | Place bulk orders for employees or operations | Reliability, invoicing, SLAs, reporting | Medium — revenue growth |

---

## 8. Target Users

### Customer Segments

| Segment | Description | Priority |
|---------|-------------|----------|
| **Busy Urban Professionals** | Working 9-6 in Dar es Salaam offices, no time for market trips, have disposable income | Primary (Launch) |
| **Small Business Owners** | Shop owners, restaurant owners who need supplies delivered to their business | Primary (Launch) |
| **Affluent Residents** | Middle-to-upper class households in planned suburbs (Mikocheni, Oyster Bay, Masaki) | Primary (Launch) |
| **Elderly / Less Mobile** | Older adults or those with limited mobility who struggle with market visits | Secondary |
| **Students** | University students in Dar (UDSM, ARU) who need supplies but have limited budgets | Secondary (V1) |
| **Tech-Adjacent Early Adopters** | Young, tech-savvy users who try new platforms and drive word-of-mouth | Primary (Launch) |

### Shopper Segments

| Segment | Description | Priority |
|---------|-------------|----------|
| **Boda-Boda Riders** | Motorcycle riders currently doing informal deliveries, seeking structured earnings | Primary (Launch) |
| **Part-Time Gig Workers** | Students or second-job seekers looking for flexible income | Primary (Launch) |
| **Career Shoppers** | Full-time platform shoppers seeking reliable income from shopping/delivery | Secondary (V1) |
| **Unemployed / Underemployed** | Individuals with smartphones and basic literacy seeking income opportunity | Secondary (V1) |

---

## 9. User Personas

### Persona 1: Aisha — The Busy Professional

| Attribute | Detail |
|-----------|--------|
| **Age** | 31 |
| **Occupation** | Marketing Manager at a Dar es Salaam telecom company |
| **Location** | Mikocheni, Dar es Salaam |
| **Income** | 2.5M TZS/month |
| **Tech Usage** | Heavy smartphone user, uses M-Pesa daily, active on WhatsApp/Instagram |
| **Pain Points** | No time to visit Kariakoo Market for fresh produce. Traffic is exhausting. Relying on informal delivery is unreliable — no tracking, inconsistent pricing. |
| **Goals** | Order groceries and household items from her office and have them arrive before she gets home. Wants real-time tracking, reliable delivery windows, and fair prices. |
| **Quote** | *"If I could order my market shopping like I order Uber, my life would change. I waste every Saturday morning at the market when I could be resting with my family."* |

### Persona 2: Juma — The Boda-Boda Rider

| Attribute | Detail |
|-----------|--------|
| **Age** | 24 |
| **Occupation** | Motorcycle rider (informal) |
| **Location** | Kariakoo, Dar es Salaam |
| **Income** | 400,000-800,000 TZS/month (inconsistent) |
| **Tech Usage** | Smartphone user (basic Android), uses M-Pesa, WhatsApp, Facebook Lite |
| **Pain Points** | Inconsistent work — waits at boda-boda stand for customers. No platform to find consistent delivery work. Some customers don't pay. No way to build a reputation. |
| **Goals** | Reliable daily income through structured delivery work. Wants to build a reputation that gets him more and better orders. Needs insurance for his motorbike. |
| **Quote** | *"Some days I make 50,000 TZS. Some days I make nothing. If I had a phone that showed me where to go and guaranteed payment, I would work all day."* |

### Persona 3: Mwajuma — The Small Shop Owner

| Attribute | Detail |
|-----------|--------|
| **Age** | 45 |
| **Occupation** | Owns a small duka (shop) in Kariakoo |
| **Location** | Kariakoo (works) / Temeke (lives) |
| **Income** | 1.2M TZS/month |
| **Tech Usage** | Moderate — uses M-Pesa daily, WhatsApp for supplier communication, but not app-savvy |
| **Pain Points** | Needs to restock supplies but cannot leave the shop. Informal delivery is unreliable for business — lost items, delays, no accountability. Current suppliers don't offer delivery. |
| **Goals** | Order inventory supplies for delivery to her shop. Needs reliable, tracked delivery with proof of delivery so she can hold someone accountable. |
| **Quote** | *"I cannot close my shop to go buy stock. If someone can bring what I need to my door, I will pay for that service every time."* |

### Persona 4: Baraka — The Platform Administrator

| Attribute | Detail |
|-----------|--------|
| **Age** | 29 |
| **Occupation** | Operations Manager at a tech startup |
| **Location** | Dar es Salaam |
| **Income** | 1.8M TZS/month |
| **Tech Usage** | Advanced — uses multiple admin dashboards, data tools, mobile apps |
| **Pain Points** | Needs real-time visibility into platform operations. Must monitor shopper activity, handle disputes, track fraud, and ensure SLAs are met. Current manual processes are inefficient. |
| **Goals** | A comprehensive admin dashboard with real-time metrics, automated fraud detection, dispute resolution workflows, and easy shopper management. Wants to scale operations without linearly scaling headcount. |
| **Quote** | *"I need to see everything at a glance — which shoppers are active, which orders are at risk, where fraud might be happening. Manual oversight doesn't scale."* |

### Persona 5: Grace — The University Student

| Attribute | Detail |
|-----------|--------|
| **Age** | 21 |
| **Occupation** | Student at University of Dar es Salaam |
| **Location** | UDSM campus, Dar es Salaam |
| **Income** | Pocket money: 200,000 TZS/month |
| **Tech Usage** | Heavy — smartphone, social media, moderate data budget |
| **Pain Points** | Limited time between classes to go shopping. Campus is far from markets. Group ordering with roommates is complicated. Budget-conscious — delivery fees matter. |
| **Goals** | Affordable delivery for small orders shared with roommates. Wants cash-on-delivery or M-Pesa. Would consider doing occasional shopping deliveries to earn extra income. |
| **Quote** | *"If three of us share an order, the delivery fee becomes nothing. But coordinating group orders is hard. If the app made it easy, we'd order every week."* |

---

## 10. Assumptions

| # | Assumption | Challenge | Validation Approach |
|---|------------|-----------|-------------------|
| A01 | Customers will pay a service fee (15-25% of order value) for on-demand shopping | Price sensitivity may be higher than expected | A/B test fee tiers at launch, measure conversion at each level |
| A02 | Sufficient smartphone penetration (36%) in Dar es Salaam to reach critical mass | Affluent neighbourhoods will adopt first, but broader market needs reach | Track neighbourhood-level adoption, consider USSD/PWA for feature phones |
| A03 | Shoppers with smartphones and transport (motorbike/bicycle) are available in sufficient numbers | May need to recruit and incentivise initial supply | Pre-launch shopper registration campaign, offer signup bonus |
| A04 | M-Pesa/Mixx integration will be stable and cost-effective for micropayments | API reliability and transaction costs may affect unit economics | Negotiate preferred rates, build retry/recovery logic |
| A05 | Tanzanian consumers trust mobile money enough for pre-payment of orders | Cash-on-delivery preference may dominate initially | Offer both pre-pay and COD, track preference shift over time |
| A06 | Markets and vendors will accept digital/unknown shoppers purchasing from them | Some vendors may refuse or overcharge unfamiliar shoppers | Build vendor trust through shopper training, initial relationship management |
| A07 | 4G/3G connectivity in Dar es Salaam is sufficient for real-time GPS tracking and chat | Network dead zones may affect reliability | Implement offline queue sync, SMS fallback for critical notifications |
| A08 | The 30-second acceptance window balances shopper decision time with customer wait | Too short = missed assignments; too long = frustrated customers | Adjust based on data: track acceptance rates by window duration |
| A09 | Customers will accept product substitutions when requested items are unavailable | May cause order cancellations or dissatisfaction | Allow customers to set substitution preferences per item (yes/no/ask) |
| A10 | Regulatory environment will remain favourable for gig economy platforms | Legislation could change rapidly | Legal counsel retainer, proactive engagement with TCRA/MIT |

---

## 11. Constraints

| # | Constraint | Impact |
|---|------------|--------|
| C01 | Limited access to traditional credit card payment infrastructure | Must build on mobile money rails (M-Pesa, Mixx, Airtel Money) |
| C02 | No standardized addressing system in Tanzanian cities | Must implement robust geo-location, landmark-based delivery addresses, potential what3words integration |
| C03 | Lower smartphone penetration (36%) limits app-only approaches | Must include PWA or USSD fallback for order placement/tracking |
| C04 | Variable network quality (particularly 2G in some areas) | Must design for offline resilience, lightweight data usage |
| C05 | Limited pool of experienced local software developers for marketplace platforms | May need to invest in training, partner with regional tech hubs, or hire remotely |
| C06 | Regulatory uncertainty around gig worker classification | Platform architecture must support both contractor and employee models |
| C07 | Cash-on-delivery preference requires handling cash transactions | Adds complexity to shopper workflow and reconciliation |
| C08 | Initial focus on Dar es Salaam only | Coverage density must be achieved before geographic expansion |
| C09 | No existing vendor/inventory integration (MVP) | Manual item entry for MVP increases friction; must be improved in V1 |
| C10 | Mobile money transaction limits may affect high-value orders | TZS 3M/day limit on M-Pesa may constrain large orders |

---

## 12. Risks

| # | Risk | Likelihood | Impact | Mitigation |
|-----|------|------------|--------|------------|
| R01 | Insufficient shopper supply leading to long assignment times | High | High | Pre-launch shopper recruitment campaign, signup bonuses, referral incentives |
| R02 | Low customer adoption due to price sensitivity or trust barriers | Medium | High | First-order subsidy, referral program, community-based marketing, money-back guarantee |
| R03 | Payment fraud (fake orders, stolen mobile money accounts) | Medium | High | Multi-factor authentication for high-value orders, fraud detection algorithms, transaction limits |
| R04 | Shopper fraud (theft of items, false delivery claims) | Medium | High | GPS verification at delivery, photo proof, rating system, shopper bond/deposit |
| R05 | Regulatory intervention on worker classification | Medium | High | Legal counsel, flexible engagement model, proactive government engagement |
| R06 | Mobile money service outages | Medium | Medium | Multi-provider integration (M-Pesa + Mixx + Airtel Money), cached balance mode |
| R07 | GPS spoofing by shoppers | Low | Medium | GPS + network triangulation, velocity checks, AI anomaly detection |
| R08 | Negative network effects from low density (long wait times → customers leave → fewer orders → shoppers leave) | High | High | Aggressive geographic focus, guaranteed assignment SLAs, demand prediction |
| R09 | Data breach or privacy incident | Low | Critical | Security-first architecture, encryption at rest and transit, regular audits, minimal data collection |
| R10 | Currency volatility (TZS/USD) affecting operating costs | Medium | Medium | Local revenue = local costs strategy; minimise USD-denominated expenses |
| R11 | Political instability or civil unrest affecting operations | Low | High | Business continuity plan, geographic diversification, insurance |
| R12 | Competitor pivoting into multi-category (Bolt, Uber) | Medium | High | First-mover advantage in dedicated shopping category, superior shopper experience, local partnerships |

---

## 13. Success Metrics

### Customer Metrics

| Metric | Target | Timeframe |
|--------|--------|-----------|
| Registered customers | 10,000+ | Month 6 |
| Active customers (ordered in last 30 days) | 3,000+ | Month 6 |
| Customer retention rate (30-day repeat order) | 40%+ | Month 9 |
| Average orders per active customer per month | 3+ | Month 9 |
| Customer satisfaction rating | 4.3+ / 5.0 | Month 6 |
| Customer acquisition cost (CAC) | < 5,000 TZS | Month 9 |

### Shopper Metrics

| Metric | Target | Timeframe |
|--------|--------|-----------|
| Active shoppers (accepted orders in last 7 days) | 500+ | Month 6 |
| Shopper acceptance rate | 80%+ | Month 6 |
| Shopper retention rate (30-day) | 70%+ | Month 9 |
| Average shopper earnings per order | 3,000-5,000 TZS | Month 6 |
| Shopper satisfaction rating | 4.0+ / 5.0 | Month 9 |

### Operational Metrics

| Metric | Target | Timeframe |
|--------|--------|-----------|
| Average assignment time (order → shopper accepts) | < 30 seconds | Month 6 |
| Average shopping time (accepted → ready for delivery) | < 45 minutes | Month 6 |
| Average delivery time (ready → delivered) | < 30 minutes | Month 6 |
| Order fulfillment rate | 95%+ | Month 6 |
| Order cancellation rate | < 5% | Month 6 |
| Dispute rate (as % of fulfilled orders) | < 1% | Month 9 |

### Financial Metrics

| Metric | Target | Timeframe |
|--------|--------|-----------|
| Gross Merchandise Value (GMV) | 1B TZS/month → 5B TZS/month | Month 6 → 18 |
| Platform commission / service fee revenue | 15-20% of GMV | Month 6 |
| Contribution margin per order | Positive | Month 12 |
| Operational breakeven (monthly) | Neutral P&L | Month 18 |

---

## 14. Value Proposition

### For Customers
- **Convenience:** Order from any market or store from your phone — no travel, no traffic, no carrying
- **Choice:** Access to any vendor, not just platform-partnered restaurants or stores
- **Trust:** Verified shoppers with ratings, real-time tracking, and photo proof of delivery
- **Time saved:** Reclaim 2-4 hours per market trip
- **Transparency:** Know what you'll pay before you order (item cost + service fee + delivery fee)

### For Shoppers
- **Flexible income:** Work when you want, as much as you want
- **Structured earnings:** Transparent, predictable pay per order
- **Reputation system:** Build a rating and profile that unlocks better opportunities
- **Safety:** GPS tracking, in-app chat, platform support
- **Growth path:** Performance tiers with increasing benefits (V1)

### For the Platform
- **Asset-light model:** No inventory, no vehicles, no dark stores
- **Network effects:** More customers → more orders → more shoppers → faster delivery → more customers
- **Data moat:** Order history, pricing data, route data, shopper performance data
- **Scalable:** One codebase, one platform model, multiple geographies

### For Markets & Vendors (Future/V2)
- **Increased sales:** Access to delivery customers without building your own infrastructure
- **No operational burden:** Platform handles shopping, delivery, and payment
- **Data insights:** Understand demand patterns, popular items, peak periods

---

## 15. Scope

### What the Platform IS (In-Scope for V1)

The Urban Shopper Platform is a **digital marketplace** that enables:

1. **Customer mobile app** (Android primary, iOS secondary) for:
   - Registration and profile management
   - Submitting shopping requests with item lists
   - Real-time order tracking (GPS)
   - In-app chat with assigned shopper
   - M-Pesa/Mixx payment integration
   - Rating and feedback

2. **Shopper mobile app** for:
   - Registration, verification, and onboarding
   - Receiving order offers with 30-second acceptance window
   - Order management (accept, shop, deliver workflow)
   - Receipt photo upload
   - GPS location sharing
   - In-app chat with customer
   - Earnings dashboard

3. **Platform backend** including:
   - GPS-based assignment engine with nearest-shopper logic
   - 30-second acceptance cascade
   - Payment processing via mobile money
   - Escrow (in V1)
   - Rating and review system
   - Admin dashboard for operations

4. **Core business workflows:**
   - Order placement → shopper assignment → shopping → delivery → payment → rating

### Geographic Scope (MVP/V1)
- **Launch:** Dar es Salaam only (Kariakoo, City Centre, Mikocheni, Oyster Bay, Masaki, Mbezi Beach, Tabata, Kimara)
- **V1 Expansion:** Arusha, Mwanza

---

## 16. Out of Scope

The following are explicitly **out of scope** for V1 and must not be designed or built in the initial platform:

| Item | Rationale | Future Version |
|------|-----------|----------------|
| Vendor/inventory management portal | Requires vendor onboarding and integration partnerships | V2 |
| Direct inventory API integration | Vendors do not have digital inventory systems in TZ | V2 |
| Corporate/business accounts | Requires invoicing, bulk ordering, credit terms | V2 |
| Multi-shop orders (one request, multiple markets) | Significant complexity in shopping workflow | V2 |
| Batch deliveries (one shopper, multiple customers) | Requires route optimization — adds operational complexity | V2 |
| AI-powered recommendation engine | Requires order volume data to train models | V2 |
| Self-hosted dark stores or inventory | Asset-light model — no inventory ownership | Future |
| Autonomous/drone delivery | Not viable in current Tanzanian regulatory/tech environment | Future |
| Fintech products (shopper loans, insurance) | Requires financial services license | Future |
| Freight/logistics services | Different business line | Future |
| Offline-first full functionality | Core features require connectivity for real-time assignment | Future |
| Food preparation / restaurant delivery | Different operational model (food is not shopping) | Future |
| Ride-hailing | Different operational model | Future |

---

## 17. Product Principles

### Principle 1: Trust is the Product
Every feature must answer: *Does this increase or decrease trust?* Verified shoppers, transparent pricing, real-time tracking, photo proof, and a fair dispute system are not features — they are the product. If a feature reduces trust, it does not ship.

### Principle 2: Density Over Reach
Launch one city, one neighbourhood at a time. Dorchester is not worth ten customers spread across ten neighbourhoods. We succeed when shoppers are within minutes of every order. Geographic concentration beats national coverage.

### Principle 3: Transparent Economics
Customers know the full cost before ordering. Shoppers know their pay before accepting. No surge pricing surprises. No opaque algorithm tricks. Transparency is our competitive advantage against incumbents.

### Principle 4: Mobile Money First
Mobile money is the default. Everything — payments, deposits, withdrawals, refunds — flows through M-Pesa, Mixx, and Airtel Money. Card payments are supplementary, not primary. Build for the 95%, not the 5%.

### Principle 5: Design for Low-Bandwidth Reality
Apps must work on 3G, recover from 2G, and survive network drops. Offline queues, compressed images, minimal payload. Because connectivity in Tanzania is improving, but it is not yet universal.

### Principle 6: Simplicity Over Feature Count
Do one thing and do it well: connect customers with shoppers. Every feature request is met with: *Does this directly help a customer get their shopping done faster, cheaper, or more reliably?* If not, defer.

### Principle 7: Fairness by Design
The platform must work well for both sides of the marketplace. We do not optimise customer experience at the expense of shopper income, nor shopper flexibility at the expense of customer reliability. The marketplace is a flywheel, not a zero-sum game.

### Principle 8: Data Privacy as a Right
Collect only what is necessary to fulfill the service. Encrypt everything. Never sell customer or shopper data. In a low-trust environment, privacy is a competitive moat.

### Principle 9: Scalable Architecture, Scaled Operations
The platform must handle 10 orders today and 10,000 orders tomorrow without a rewrite. But operations (shopper support, dispute resolution, vendor relationships) should scale only when needed. Build the technical foundation; grow the team when demand proves itself.

### Principle 10: Local First, Global Ready
Every decision is made for the Tanzanian context — mobile money, Swahili language support, market-based shopping, boda-boda logistics. But the platform architecture must support new geographies without major rework. The model extends; the code is localised.

---

*This document feeds into Business Rules Analysis (`03-business-rules.md`) and Workflow Modelling (`04-business-workflows.md`).*
