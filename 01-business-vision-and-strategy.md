# Urban Shopper Platform — Business Vision & Product Strategy

> **Document Type:** Business Strategy  
> **Status:** Complete  
> **Date:** 2026-07-24  
> **Version:** 1.0  
> **Cross-Reference:** Market Research — `01-market-research.md`, Business Rules — `03-business-rules.md`, Release Scope — `05-release-scope.md`  
> **Supersedes:** `02-vision-product-definition.md` (earlier draft)

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Vision Statement](#2-vision-statement)
3. [Mission Statement](#3-mission-statement)
4. [Product Goals](#4-product-goals)
5. [Value Proposition](#5-value-proposition)
6. [Problem Statement](#6-problem-statement)
7. [Market Opportunity](#7-market-opportunity)
8. [Customer Segments](#8-customer-segments)
9. [Shopper Segments](#9-shopper-segments)
10. [Competitive Positioning](#10-competitive-positioning)
11. [Business Model](#11-business-model)
12. [Revenue Streams](#12-revenue-streams)
13. [Cost Structure](#13-cost-structure)
14. [Growth Strategy](#14-growth-strategy)
15. [Version 1 Scope](#15-version-1-scope)
16. [Version 2 Roadmap](#16-version-2-roadmap)
17. [Success Metrics (KPIs)](#17-success-metrics-kpis)
18. [Risks and Assumptions](#18-risks-and-assumptions)

---

## 1. Executive Summary

**Urban Shopper** is a digital marketplace connecting urban consumers in Tanzania with verified independent shoppers who purchase and deliver items from any market or store. It fills a gap that food-only platforms (Uber Eats), package couriers (Bolt Send), and informal boda-boda riders do not serve: **multi-category, any-store shopping with verification, tracking, and accountability.**

The platform targets Tanzania at an inflection point: 63M mobile money accounts, 88% 4G coverage, 36% smartphone penetration and rising, and a fragmented competitive landscape with no dominant player in multi-category delivery.

**The Business Model:** Urban Shopper charges a tiered service fee (8-15% of item value) on each completed order plus a delivery fee. Shoppers are paid a tiered shopping fee plus the delivery fee. The model is asset-light — no inventory, no vehicles, no dark stores.

**Launch:** MVP in Dar es Salaam (Android, M-Pesa/Mixx payments, GPS-based assignment with 30-second acceptance). Target: 500 active shoppers and 10,000 registered customers within 6 months.

**Key metrics:** 5B TZS/month GMV by Month 18, positive contribution margin per order by Month 12, operational breakeven by Month 18.

**134 business rules** have been defined, reviewed, and locked across 13 categories covering every aspect of the platform — from pricing and assignment through to cancellation, fraud prevention, and emergency handling.

---

## 2. Vision Statement

To become Africa's most trusted on-demand marketplace — where anyone, anywhere, can get anything from any market, delivered by a verified shopper, within hours.

---

## 3. Mission Statement

To connect urban consumers with trusted independent shoppers who can purchase and deliver items from any market or store, creating economic opportunity for shoppers while delivering convenience and reliability for customers.

---

## 4. Product Goals

| # | Goal | Timeframe | Measure |
|---|------|-----------|---------|
| G1 | Launch MVP in Dar es Salaam with 500+ active shoppers and 10,000+ registered customers | Month 6 | Active user counts |
| G2 | Achieve < 30-second average assignment-to-acceptance time | Month 6 | System metric |
| G3 | Maintain 95%+ order fulfillment rate | Month 6 | Fulfillment rate |
| G4 | Expand to 3 Tanzanian cities (Dar es Salaam, Arusha, Mwanza) | Month 18 | Cities live |
| G5 | Process 100,000+ orders per month | Month 18 | Monthly order volume |
| G6 | Achieve positive unit economics per order | Month 12 | Contribution margin |
| G7 | Achieve average customer rating of 4.3+/5 | Month 9 | Average rating |
| G8 | Maintain shopper acceptance rate above 80% | Month 6 | Acceptance rate |

---

## 5. Value Proposition

### For Customers
- **Convenience:** Order from any market from your phone — no travel, no traffic, no carrying heavy bags
- **Choice:** Access to any vendor, not just platform-partnered stores or restaurants
- **Trust:** Verified shoppers with ratings, real-time tracking, and photo proof of delivery
- **Time saved:** Reclaim 2-4 hours per market trip
- **Transparency:** Know the full cost before ordering (item cost + tiered service fee + delivery fee)

### For Shoppers
- **Flexible income:** Work when you want, as much as you want. 48-hour settlement
- **Structured earnings:** Transparent, predictable pay per order — tiered shopping fee (2,500/4,000/6,000/2%) plus delivery fee
- **Reputation system:** Build a profile with ratings, badges, and performance tiers that unlock better opportunities
- **Safety:** GPS tracking, in-app chat, platform support, emergency procedures
- **Growth path:** Performance tiers with priority access, recognition, and early feature access (V1)

### For the Platform
- **Asset-light model:** No inventory, no vehicles, no dark stores, no warehouses
- **Network effects:** More customers → more orders → more shoppers → faster delivery → more customers
- **Data moat:** Order history, pricing data, route data, shopper performance — accumulated switching costs
- **Scalable:** One codebase, one platform model, extensible to multiple cities and countries

### For Markets & Vendors (Future — V2)
- **Increased sales:** Access to delivery customers without building delivery infrastructure
- **No operational burden:** Platform handles shopping, delivery, and payment
- **Data insights:** Demand patterns, popular items, peak periods

---

## 6. Problem Statement

### For Customers
Urban residents in Tanzania spend significant time and money travelling to markets, navigating traffic, haggling, and carrying purchases home. Existing delivery options are fragmented:

- **Food-only platforms** (Uber Eats) — cannot order fresh produce, household goods, or electronics
- **Informal boda-boda riders** — no reliability, no tracking, no accountability, no insurance
- **Self-transport** — time-consuming, expensive, exhausting

**No single platform** exists where a customer can request items from any market or store and have them shopped and delivered by a verified, tracked, and rated shopper.

### For Shoppers
Millions of capable individuals (boda-boda riders, students, part-time workers) lack a structured platform to monetise their time and mobility. They work informally — inconsistent income, no reputation system, no safety net, no growth path. Existing gig platforms either exclude them or offer poor terms.

### For Markets & Small Vendors
Traditional Tanzanian markets have no digital storefront. They cannot offer delivery, accept digital payments, or reach customers beyond foot traffic. They are excluded from the e-commerce revolution.

---

## 7. Market Opportunity

Tanzania is at an inflection point for a platform like Urban Shopper:

| Factor | Data | Implication |
|--------|------|-------------|
| **Mobile money accounts** | 63.2M active (Dec 2024) | Payment infrastructure is ubiquitous and mature |
| **4G coverage** | 88% of population | Sufficient for real-time GPS and chat |
| **Smartphone penetration** | 36% and growing rapidly | Addressable market expands yearly |
| **Mobile internet speed** | 22.61 Mbps median (+56% YoY) | Improving connectivity supports rich app features |
| **Urban population (Dar)** | ~7M | Sufficient density for marketplace network effects |
| **Competitive landscape** | Fragmented — no dominant multi-category player | First-mover opportunity in open territory |

**Key gap:** Food delivery exists (Uber Eats). Package courier exists (Bolt Send). But **multi-category market shopping with verification, tracking, and structured earnings** does not exist as a dedicated platform in Tanzania.

**Market size:** Tanzania's e-commerce generated over US$536M in 2025, growing 15-20% annually. Online food delivery alone is projected at significant scale with 19%+ annual growth. Grocery and general retail delivery is largely untapped.

---

## 8. Customer Segments

| Segment | Description | Priority | Acquisition Strategy |
|---------|-------------|----------|---------------------|
| **Busy Urban Professionals** | Working 9-6 in Dar offices, no time for market trips, have disposable income | Primary (Launch) | Workplace referral programmes, LinkedIn/Instagram ads |
| **Small Business Owners** | Shop/restaurant owners needing supplies delivered to their business | Primary (Launch) | Direct outreach to business districts (Kariakoo) |
| **Affluent Residents** | Middle-to-upper class households in Mikocheni, Oyster Bay, Masaki | Primary (Launch) | Neighbourhood-specific campaigns, community WhatsApp groups |
| **Tech-Adjacent Early Adopters** | Young, tech-savvy users who drive word-of-mouth | Primary (Launch) | Social media, university partnerships, influencer trials |
| **Elderly / Less Mobile** | Older adults with limited mobility | Secondary (V1) | Referral from family members, USSD/PWA fallback |
| **Students** | University students with limited budgets | Secondary (V1) | Group ordering features, campus ambassadors |

---

## 9. Shopper Segments

| Segment | Description | Priority | Recruitment Strategy |
|---------|-------------|----------|---------------------|
| **Boda-Boda Riders** | Motorcycle riders doing informal deliveries | Primary (Launch) | Boda-boda stand partnerships, signup bonuses |
| **Part-Time Gig Workers** | Students and second-job seekers | Primary (Launch) | Online ads, university noticeboards |
| **Career Shoppers** | Full-time platform shoppers seeking reliable income | Secondary (V1) | Performance-based upgrade from standard shopper |
| **Unemployed / Underemployed** | Individuals with smartphones seeking income | Secondary (V1) | Community centre outreach, referral bonuses |

---

## 10. Competitive Positioning

### Direct Competitors

| Competitor | Scope | Strength | Weakness | Our Advantage |
|------------|-------|----------|----------|---------------|
| **Uber Eats** | Food delivery (Dar) | Brand, logistics infrastructure | Food only, high commissions (25-30%) | Multi-category, lower fees (8-15%), market shopping |
| **Bolt Send** | Package courier (Dar, launched Sep 2024) | Existing rider network | Packages only, no shopping service | Shopping + delivery in one service |
| **Piki** | Local food/drinks/grocery | Local knowledge | Limited geographic coverage, food focus | Verified shoppers, tracking, structured earnings |
| **Informal boda-boda** | Ad-hoc goods transport | Cheap, ubiquitous | No reliability, tracking, or accountability | Verification, ratings, proof of delivery, insurance |

### Key Differentiators

1. **Multi-category, any-store shopping** — not limited to restaurant food or packages
2. **Verified shoppers** — ID verification, background checks, ratings, performance tiers
3. **Transparent pricing** — tiered fees (8-15%), road-distance delivery fees, no surge surprises
4. **Mobile money native** — M-Pesa/Mixx from Day 1, not as an afterthought
5. **Fast settlement** — 48-hour shopper payout vs industry standard of weekly
6. **Fair assignment** — weighted Assignment Score (Distance + Quality + Fairness), not just nearest
7. **Offline resilience** — designed for 3G/2G, offline queues, SMS fallback

---

## 11. Business Model

Urban Shopper operates a **two-sided marketplace** connecting customers (demand) with independent shoppers (supply). The platform does not own inventory, vehicles, or stores.

### How It Works
1. Customer submits a shopping request with item list and preferences
2. Platform assigns the nearest eligible shopper (weighted Assignment Score)
3. Shopper purchases items at the market, uploads receipt
4. Shopper delivers items to the customer
5. Customer pays (mobile money or COD via Trust Score)
6. Platform collects service fee, pays shopper, retains margin

### Unit Economics (Illustrative)

**Typical order: 30,000 TZS item cost, 4 km delivery**

| Line Item | Amount (TZS) | % of Item Cost |
|-----------|:------------:|:--------------:|
| Item cost | 30,000 | 100% |
| Platform service fee (10%) | 3,000 | 10% |
| Delivery fee (1,500 base + 4×500) | 3,500 | 11.7% |
| **Total customer pays** | **36,500** | — |
| Shopper payout (delivery fee + shopping fee) | 3,500 + 2,500 = 6,000 | — |
| **Platform gross margin** | 3,000 - platform costs | — |

*Note: Delivery fee and shopping fee are paid to the shopper. Platform revenue is the service fee minus payment processing, support, and infrastructure costs.*

---

## 12. Revenue Streams

| Revenue Stream | Description | Timing |
|----------------|-------------|--------|
| **Platform Service Fee** | Tiered: 8%/10%/12%/15% of item cost based on order value (F-001) | MVP |
| **Delivery Fee** | Zone-configurable base + per-km rate (F-002) — passed through to shopper (not revenue, but fee structure affects demand) | MVP |
| **Promotional Credits** | Unused credits expire (breakage revenue) | V1 |
| **Advertising / Promoted Listings** | Vendors pay for priority placement (future) | V2 |
| **Corporate Accounts** | Monthly subscription + per-order fee for business clients | V2 |
| **Data Insights** | Anonymised market data sold to FMCG companies and vendors | V2+ |
| **Fintech Partnerships** | Commission from partner lenders for shopper loans | V2+ |

---

## 13. Cost Structure

| Cost Category | Description | Timing | Scaling Characteristic |
|---------------|-------------|--------|----------------------|
| **Payment processing fees** | M-Pesa/Mixx transaction charges (~1-3%) | MVP | Variable — scales with GMV |
| **SMS costs** | OTP, notifications, fallback communications | MVP | Variable — scales with user count |
| **Maps API** | Distance calculations, route estimation | MVP | Variable — scales with orders |
| **Cloud infrastructure** | Servers, database, bandwidth, CDN | MVP | Step-function — scales with load |
| **Shopper protection payouts** | Stage-based compensation for cancelled orders (F-011) | MVP | Variable — % of orders |
| **Promotions & subsidies** | First-order discount (F-007), referral credits (F-008) | MVP | Variable — controlled via caps |
| **Customer support team** | Salaries for support agents | V1 | Step-function — scales with volume |
| **Administration & operations** | Salaries for ops, management, compliance | MVP | Fixed + step-function |
| **Legal & compliance** | Regulatory counsel, licensing | MVP | Fixed |
| **Office & overhead** | Rent, utilities, equipment | MVP | Fixed |
| **Insurance premiums** | Shopper accident insurance (M-009) | V2+ | Variable — per active shopper |
| **Marketing & acquisition** | Digital ads, community outreach, signup bonuses | MVP | Variable — controlled per channel |

---

## 14. Growth Strategy

### Phase 1: Density (Months 1-6)
**Objective:** Achieve critical mass in Dar es Salaam
- Launch in select high-density zones (Masaki, Oyster Bay, Mikocheni, City Centre, Kariakoo)
- Minimum 20 active shoppers per zone before accepting customer orders
- Supply-first recruitment — signup bonuses for shoppers, referral incentives
- First-order delivery discount to drive trial
- Community-based marketing (WhatsApp groups, neighbourhood ambassadors)

### Phase 2: Expand (Months 6-12)
**Objective:** Cover all Dar es Salaam + add V1 features
- Extend to remaining Dar zones (Tabata, Kimara, Mbezi, outer areas)
- Launch scheduled orders, iOS app, live map tracking
- Introduce performance tiers and promotions engine
- Build escrow integration for payment trust
- Focus on retention metrics (30-day repeat order rate, shopper retention)

### Phase 3: Multi-City (Months 12-18)
**Objective:** Expand to Arusha and Mwanza
- Apply lessons from Dar to each new city
- Localised shopper recruitment per city
- Zone configuration tailored to each city's geography and market density
- Target: 100,000 orders/month across all cities

### Phase 4: Deepen (Months 18-24)
**Objective:** Build moats through platform depth
- Vendor integration portal — transition from manual shopping to digital ordering
- Multi-shop orders — one request, multiple vendors
- Corporate accounts for B2B revenue
- Loyalty programme to increase switching costs
- AI demand prediction for supply management

### Phase 5: Regional (24+ months)
**Objective:** East African expansion (Kenya, Uganda, Rwanda)
- Triggered by business readiness criteria (M-007): positive unit economics, stable ops, customer satisfaction, operational capacity, regulatory readiness
- Localised for each market (mobile money provider, language, currency)

---

## 15. Version 1 Scope

### Included in V1

| Area | Scope |
|------|-------|
| **Customer App** | Android app, registration (phone OTP), item entry with optional fields (brand, unit, max price), shopping preference selection, order tracking (status-based), in-app chat, rating, M-Pesa/Mixx direct payments |
| **Shopper App** | Android app, registration with ID upload + NIDA/PCC verification, GPS pings (tiered by state), 30-sec offer with countdown, item status tracking, receipt upload (single/multiple/handwritten/no-receipt), in-app chat, earnings dashboard, 48-hr settlement |
| **Assignment Engine** | Hybrid Haversine → Road distance, multi-factor Assignment Score (C-011), 3-min cascade, per-zone configurable radius |
| **Payments** | Direct M-Pesa/Mixx (no wallet), pre-auth holds, tiered service fee (8-15%), zone-configurable delivery fee, tiered shopping fee (2,500/4,000/6,000/2%), COD via Customer Trust Score |
| **Disputes** | Unified Dispute Resolution Framework (G-011), automated triage, manual review |
| **Admin Dashboard** | Shopper vetting, dispute resolution, fraud alerts, basic metrics, zone management |
| **Support** | In-app issue reporting, email support, manual dispute resolution |
| **Language** | Swahili + English |

### Explicitly Not in V1

| Item | Reason | Version |
|------|--------|---------|
| iOS native apps | 90%+ Android market share in Tanzania | V1 |
| In-platform wallet | Adds significant complexity; direct payments sufficient for MVP | V1 |
| Live map tracking | Status-based tracking sufficient for MVP | V1 |
| Escrow | Requires legal/financial licensing assessment | V1 |
| Shopper bond/deposit | May deter initial signups | Deferred |
| Vendor inventory integration | Vendors lack digital systems | V2 |
| Multi-shop orders | Significant workflow complexity | V2 |
| Corporate accounts | Requires invoicing, NET-30, credit management | V2 |
| Batch delivery | Route optimization complexity | V2 |
| AI/ML features | Requires order volume data | V2+ |
| International expansion | Premature before Tanzanian density achieved | 24+ months |

---

## 16. Version 2 Roadmap

| Feature | Rationale | Target |
|---------|-----------|--------|
| **iOS apps** (Customer + Shopper) | Address iOS minority market share | Month 7-8 |
| **Live map tracking** | Customer expectation for modern delivery | Month 7-8 |
| **Escrow integration** | Build payment trust without direct holds | Month 8-9 |
| **Promotions engine** | Admin-configurable promo codes and discounts | Month 8-9 |
| **Shopper performance tiers** | Incentivise quality through priority access | Month 8-9 |
| **Wallet (customer)** | Enable credits, refunds, promo balance | Month 9-10 |
| **Scheduled orders** | Allow customers to order for future delivery | Month 9-10 |
| **PWA/USSD fallback** | Address feature phone segment (64% of market) | Month 10-11 |
| **Support ticket system** | Scale support without scaling headcount linearly | Month 10-11 |
| **Advanced fraud detection** | Pattern-based and ML-assisted detection | Month 11-12 |
| **Multi-city expansion** | Arusha, then Mwanza | Month 12-18 |
| **Vendor integration portal** | First step toward digital inventory | Month 12-15 |
| **Multi-shop orders** | One customer request, multiple vendor fulfillment | Month 15-18 |
| **Corporate accounts** | B2B revenue stream | Month 15-18 |
| **Loyalty programme** | Points + tiers to increase switching costs | Month 15-18 |

---

## 17. Success Metrics (KPIs)

### Customer KPIs

| Metric | MVP Target | V1 Target |
|--------|:----------:|:---------:|
| Registered customers | 10,000+ (M6) | 50,000+ (M12) |
| Monthly active customers | 3,000+ (M6) | 15,000+ (M12) |
| 30-day repeat order rate | 40%+ (M9) | 50%+ (M12) |
| Avg orders per active customer/month | 3+ (M9) | 4+ (M12) |
| Customer satisfaction rating | 4.3+/5 (M6) | 4.5+/5 (M12) |
| Customer acquisition cost (CAC) | < 5,000 TZS (M9) | < 4,000 TZS (M12) |

### Shopper KPIs

| Metric | MVP Target | V1 Target |
|--------|:----------:|:---------:|
| Active shoppers (orders in last 7 days) | 500+ (M6) | 2,000+ (M12) |
| Shopper acceptance rate | 80%+ (M6) | 85%+ (M12) |
| Shopper retention rate (30-day) | 70%+ (M9) | 75%+ (M12) |
| Avg shopper earnings per order | 3,000-5,000 TZS (M6) | 4,000-6,000 TZS (M12) |

### Operational KPIs

| Metric | MVP Target | V1 Target |
|--------|:----------:|:---------:|
| Avg assignment time | < 30 sec (M6) | < 20 sec (M12) |
| Avg shopping time (est.) | Per item-count tier (M6) | Per item-count tier (M12) |
| Avg delivery time | Dynamic ETA (M6) | Dynamic ETA (M12) |
| Order fulfillment rate | 95%+ (M6) | 97%+ (M12) |
| Order cancellation rate | < 5% (M6) | < 3% (M12) |
| Dispute rate | < 1% (M9) | < 0.5% (M12) |

### Financial KPIs

| Metric | MVP Target | V1 Target |
|--------|:----------:|:---------:|
| Monthly GMV | 1B TZS (M6) | 5B TZS (M12) |
| Contribution margin per order | Negative (investing) | Positive (M12) |
| Operational breakeven | — | Neutral P&L (M18) |

---

## 18. Risks and Assumptions

### Key Assumptions

| # | Assumption | Impact if False | Validation |
|---|------------|-----------------|------------|
| A1 | Customers will pay 8-15% service fee for on-demand shopping | Revenue model breaks | A/B test fee tiers, measure conversion |
| A2 | Sufficient smartphone users in Dar to reach critical mass | Slower adoption | Track neighbourhood-level adoption; PWA/USSD fallback in V1 |
| A3 | Sufficient shoppers with smartphones + transport available | Long assignment times, poor experience | Pre-launch recruitment campaign, signup bonuses |
| A4 | M-Pesa/Mixx APIs will be reliable for transaction processing | Payment failures erode trust | Multi-provider integration, retry logic, fallback to COD |
| A5 | Customers will accept mobile money pre-payment | COD dominates, complicating operations | Offer both pre-pay and COD via Trust Score |
| A6 | Markets will accept shoppers purchasing on behalf of customers | Shopping workflow breaks | Shopper training, market relationship management |
| A7 | 4G/3G in Dar sufficient for real-time GPS and chat | Poor tracking, missed assignments | Offline queues, SMS fallback, lightweight protocol |

### Key Risks

| # | Risk | Likelihood | Impact | Mitigation |
|---|------|:----------:|:------:|------------|
| R1 | Insufficient shopper supply | High | High | Pre-launch recruitment, signup bonuses, referral incentives |
| R2 | Low customer adoption (price/trust) | Medium | High | First-order subsidy, referral programme, money-back guarantee |
| R3 | Payment fraud | Medium | High | Multi-factor auth, fraud detection, transaction limits |
| R4 | Shopper fraud/theft | Medium | High | GPS verification, photo proof, ratings, stage-based protection (F-011) |
| R5 | Regulatory intervention on worker classification | Medium | High | Flexible engagement model, proactive government engagement |
| R6 | Mobile money service outages | Medium | Medium | Multi-provider, cached balance mode |
| R7 | Negative network effects (low density → poor experience → churn) | High | High | Aggressive geographic focus, guaranteed assignment SLAs |
| R8 | Data breach / privacy incident | Low | Critical | Security-first architecture, encryption, minimal data collection |
| R9 | Competitor pivots into multi-category | Medium | High | First-mover advantage, local partnerships, superior shopper experience |
| R10 | Currency volatility (TZS/USD) | Medium | Medium | Local revenue = local costs strategy |

---

*This document is Phase 1 of the Urban Shopper Platform specification. It feeds into Phase 2 (Domain Model), Phase 3 (Business Rules — complete), and Phase 4 (State Machines).*
