# Urban Shopper Platform — Project Summary

> **A complete digital marketplace specification from business analysis through implementation planning.**  
> **Total effort:** 12 phases, 14 documents, ~7,000+ lines of specification  
> **Date:** 2026-07-24  
> **Status:** Specification Complete — Ready for Development

---

## 1. The Vision

Urban Shopper is a digital marketplace connecting urban consumers in Tanzania with verified independent shoppers who purchase and deliver items from any market or store. It fills a gap no existing platform serves: **multi-category, any-store shopping with verification, tracking, and accountability.**

| Aspect | Detail |
|--------|--------|
| **Target Market** | Urban Tanzania (launch: Dar es Salaam) |
| **Launch Timeline** | 20 weeks to hard launch |
| **Business Model** | Asset-light marketplace — no inventory, no vehicles, no dark stores |
| **Revenue** | Tiered service fee (8-15% of item value) |
| **Payments** | M-Pesa, Mixx, Airtel Money + Cash on Delivery |
| **Shopper Payout** | Tiered shopping fee + delivery fee, settled in 48 hours |

---

## 2. Documentation Suite

| # | Phase | Document | Pages Est. | Key Content |
|:-:|-------|----------|:----------:|-------------|
| **1** | Business Vision & Strategy | `01-business-vision-and-strategy.md` | 15 | Executive summary, vision/mission, problem/opportunity, customer/shopper segments, competitive positioning, business model, revenue streams, cost structure, 5-phase growth strategy, V1 scope, V2 roadmap, KPIs, risks |
| **2** | Domain Model | `02-domain-model.md` | 15 | 22 entities with purpose, responsibilities, lifecycle, attributes, business rules, relationships, ER diagram, 15 relationship rules |
| **3** | Business Rules | `03-business-rules.md` | 15 | **134 rules** across 13 categories, each with justification, priority, dependencies, exceptions |
| **4** | State Machines | `04-state-machines.md` | 15 | **4 machines**: Order (13 states), Shopper (8 states), Payment (7 states), Dispute (6 states) |
| **5** | BPMN Process Models | `05-business-process-models.md` | 15 | **6 processes**: Ordering, Acceptance, Shopping, Delivery, Refund, Dispute |
| **6** | Non-Functional Requirements | `06-non-functional-requirements.md` | 15 | **118 NFRs** across 13 categories (performance, security, scalability, availability, data, UX, localisation, compliance, DR, monitoring, maintainability, interoperability) |
| **7** | System Architecture | `07-system-architecture.md` | 15 | 9 microservices, technology stack, 80+ API endpoints, deployment architecture, security architecture, caching strategy, performance budgets |
| **8** | IEEE 29148 SRS | `08-ieee-29148-srs.md` | 30 | **96 functional requirements**, 14 use cases, 45 configurable parameters, 10 implementation constraints, 8 integration assumptions, full traceability matrix, 15 acceptance test scenarios |
| **9** | UX Specification | `09-ux-specification.md` | 15 | 11 customer screens, 5 shopper screens, 7 admin screens, error/loading/empty states, 14 notification templates |
| **10** | API Specification | `10-api-specification.md` | 15 | ~80 REST endpoints + 3 WebSocket channels across 10 resource groups. Full request/response formats |
| **11** | Database Design | `11-database-design.md` | 15 | 20 tables across 8 schemas. TimescaleDB hypertable for GPS. Indexing, partitioning, key query optimisation |
| **12** | Implementation Roadmap | `12-implementation-roadmap.md` | 10 | 7 phases (0-6), key milestones (M0-M8), task breakdowns, team composition (9→20), risk register, dependency graph |
| **S1** | Market Research | `01-market-research.md` | 10 | 10 global competitor analyses. Tanzanian market: mobile money, connectivity, regulation, gaps |
| **S2** | Release Scope | `05-release-scope.md` | 10 | MVP/V1/V2/Future scope boundaries. 12 scope decisions with rationale |
| | **README** | `README.md` | 5 | Project overview, document index, quick reference |

---

## 3. Key Statistics

### Specification Scope

| Metric | Value |
|--------|:-----:|
| Total documents | **14** |
| Total estimated lines | **~7,000+** |
| Business rules (final, refined) | **134** |
| Functional requirements (SRS) | **96** |
| Non-functional requirements | **118** |
| Use cases (with exception flows) | **14** |
| Domain entities | **22** |
| State machines | **4** |
| Acceptance test scenarios | **15** |
| Configurable business parameters | **45** |
| API endpoints | **~80+** |
| Database tables | **20** |

### Business Model

| Component | Detail |
|-----------|--------|
| Platform fee (tiered) | 8% / 10% / 12% / 15% |
| Delivery fee | Zone-configurable base + per-km (road distance) |
| Shopping fee (tiered) | 2,500 / 4,000 / 6,000 / 2% (max 15,000) TZS |
| Shopper settlement | 48 hours |
| Service fee tiers react to order value: small orders pay less, large orders pay more |

### Rules Distribution

| Priority | Count | Scope |
|:--------:|:-----:|-------|
| Critical | 50 | Platform won't function without these |
| High | 48 | Essential for MVP |
| Medium | 24 | Important, V1 delivery |
| Low | 12 | Enhancements, V2+ |
| **Total** | **134** | |

---

## 4. Architecture at a Glance

```
┌─────────────┐  ┌─────────────┐  ┌──────────────────┐
│ Customer    │  │ Shopper     │  │ Admin Dashboard  │
│ App         │  │ App         │  │ (Web / PWA)      │
│ (Android/iOS)│  │ (Android/iOS)│  │                  │
└──────┬──────┘  └──────┬──────┘  └────────┬─────────┘
       │                │                   │
       └────────────────┼───────────────────┘
                        │
               ┌────────┴────────┐      External Integrations:
               │   API Gateway   │      ┌─────────────────┐
               └────────┬────────┘      │ · M-Pesa / Mixx  │
                        │               │ · SMS Gateway    │
          ┌─────────────┼──────────┐    │ · Maps API      │
          │  Message Broker       │    │ · FCM Push       │
          │  (RabbitMQ/Kafka)     │    │ · NIDA (future)  │
          └─────────────┼──────────┘    └─────────────────┘
                        │
    ┌───────────────────┼───────────────────┐
    │       9 Microservices + Admin         │
    ├───────────────────────────────────────┤
    │ User | Order | Assignment | Payment  │
    │ Delivery | Dispute | Rating | Notif  │
    │ GPS Ingestion + Fraud Detection      │
    └───────────────────────────────────────┘
                        │
          ┌─────────────┼─────────────┐
          │             │             │
    ┌─────┴─────┐ ┌────┴────┐ ┌─────┴─────┐
    │PostgreSQL │ │  Redis  │ │ Timescale │
    │(primary)  │ │ (cache) │ │  (GPS)    │
    └───────────┘ └─────────┘ └───────────┘
```

**Technology stack:** Flutter (customer + shopper apps), Next.js + TypeScript (admin), Spring Boot 3.x + Java 21 (backend), PostgreSQL + PostGIS, Redis, RabbitMQ, Docker Compose + Nginx, Prometheus + Grafana, Sentry, GitHub Actions.

---

## 5. Key Design Decisions Made During Specification

| Decision | Rationale |
|----------|-----------|
| **Tiered service fee** (not flat %) | Small orders stay affordable (8%), large orders generate fair revenue (15%) |
| **Road distance** (not straight-line) | Accurate delivery fee calculation; fair to shoppers |
| **Hybrid distance** (Haversine → Road for top 20) | Performance optimization: avoids 100+ Maps API calls per assignment |
| **Assignment Score** (not nearest-only) | Multi-factor (Distance + Quality + Fairness) prevents workload imbalance |
| **3-minute cascade** (not 5) | Faster feedback to customer when no shoppers available |
| **State machine enforcement** | Prevents invalid order transitions; simplifies testing |
| **48-hour settlement** (not weekly) | Competitive advantage for shopper recruitment |
| **Staged shopper protection** (not flat) | Fair compensation based on actual effort invested |
| **Proportional refund tiers** (not fixed %) | A single damaged tomato ≠ 50% of order wrong |
| **Customer Trust Score** (not 5-order rule) | Multi-dimensional reliability assessment, harder to game |
| **Discretionary goodwill** (not automatic) | Prevents unnecessary financial loss; case-by-case fairness |
| **Reputation Recovery** (not permanent rating) | Encourages continuous improvement; reduces shopper churn |
| **Unified Dispute Framework** (not per-type) | Consistent lifecycle regardless of dispute type; simpler support training |
| **Android-first MVP** (not iOS) | 90%+ market share in Tanzania |
| **Direct M-Pesa payments** (not wallet-first) | Simpler MVP; wallet adds regulatory and technical complexity |
| **Configurable parameters** (not hard-coded) | Business can adapt pricing, timers, and thresholds without code changes |

---

## 6. Development Roadmap

| Phase | Timeline | Focus | Team |
|:-----:|:--------:|-------|:----:|
| **0** Foundation | Weeks 1-4 | Infrastructure, CI/CD, auth, database, dev environment | 9 |
| **1** Core MVP | Weeks 5-12 | Complete order loop: register → order → assign → shop → deliver → pay → rate | 9 |
| **2** MVP Launch | Weeks 13-20 | Admin dashboard, fraud detection, load testing, soft launch, hard launch | 9 |
| **3** Stabilise | Weeks 21-28 | Bug fixes, iOS apps, PWA, performance tuning | 11 |
| **4** V1 Features | Weeks 29-40 | Escrow, scheduled orders, promotions, support system, multi-language | 13 |
| **5** Scale | Weeks 41-52 | Multi-city (Arusha, Mwanza), optimisation, cost management | 13 |
| **6** V2 | Months 13-18 | Vendor portal, multi-shop, corporate accounts, loyalty, AI features | ~20 |

---

## 7. Commercial Viability Indicators

| Metric | MVP Target (M6) | V1 Target (M12) |
|--------|:---------------:|:---------------:|
| Registered customers | 10,000+ | 50,000+ |
| Active shoppers | 500+ | 2,000+ |
| Monthly GMV | 1B TZS | 5B TZS |
| Order fulfillment rate | 95%+ | 97%+ |
| Customer retention (30-day) | 40%+ | 50%+ |
| Contribution margin per order | Negative (investing) | Positive |

---

## 8. Document Location

All files are in the project directory: `/home/khalil/Desktop/DANGER/Urban_Shopper/`

| Quick Access | File |
|-------------|------|
| Full reading order | `README.md` |
| Start here for business context | `01-business-vision-and-strategy.md` |
| All business rules (the "constitution") | `03-business-rules.md` |
| Full requirements specification | `08-ieee-29148-srs.md` |
| Implementation plan | `12-implementation-roadmap.md` |

---

*This specification was produced through a systematic business analysis process following IEEE 29148-2018 standards. Every business rule was defined and refined before requirements were written. The result is a professional-grade specification suitable for guiding the development of a commercial marketplace platform.*

**Date:** 2026-07-24  
**Status:** ✅ Specification Complete — Ready for Development  
**Next Step:** Phase 0 — Foundation (Infrastructure, CI/CD, Auth, Database)
