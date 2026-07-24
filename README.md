# Urban Shopper Platform — Complete Specification

> **A digital marketplace connecting customers with verified independent shoppers in Tanzania.**  
> *Phase: Specification Complete | Status: Ready for Development*

---

## Overview

Urban Shopper is a GPS-enabled digital marketplace that connects customers with verified independent shoppers. Customers submit shopping requests for items from any market or store. The platform automatically assigns the nearest available shopper (using a weighted Assignment Score with a 30-second acceptance cascade). The assigned shopper purchases the requested items and delivers them to the customer.

- **Target Market:** Urban Tanzania (launch: Dar es Salaam)
- **Payment Model:** Mobile money (M-Pesa, Mixx, Airtel Money) + Cash on Delivery
- **Revenue:** Tiered service fee (8-15% of item value)
- **Business Model:** Asset-light marketplace (no inventory, no vehicles, no dark stores)

---

## Specification Documents

| Phase | Document | Description |
|:-----:|----------|-------------|
| **1** | [`01-business-vision-and-strategy.md`](01-business-vision-and-strategy.md) | Executive summary, vision, mission, value proposition, market opportunity, customer/shopper segments, competitive positioning, business model, revenue streams, cost structure, growth strategy, V1 scope, V2 roadmap, KPIs, risks |
| **2** | [`02-domain-model.md`](02-domain-model.md) | 22 core business entities with purpose, responsibilities, lifecycle, attributes, business rules, relationships, and ER diagram |
| **3** | [`03-business-rules.md`](03-business-rules.md) | **134** business rules across 13 categories (Customer, Shopper, GPS/Assignment, Order, Delivery, Payments, Cancellations, Ratings, Fraud, Communication, Emergency, Admin, Future) |
| **4** | [`04-state-machines.md`](04-state-machines.md) | 4 formal state machines: Order (13 states), Shopper (8 states), Payment (7 states), Dispute (6 states) with transition tables, trigger events, and validation rules |
| **5** | [`05-business-process-models.md`](05-business-process-models.md) | 6 BPMN process models: Customer Ordering, Shopper Acceptance, Shopping, Delivery, Refund, Dispute Resolution — with swimlanes, gateways, and business rule cross-references |
| **6** | [`06-non-functional-requirements.md`](06-non-functional-requirements.md) | **118** non-functional requirements across 13 categories: Performance, Scalability, Availability, Security, Data, Usability, Localisation, Compliance, Disaster Recovery, Monitoring, Maintainability, Interoperability |
| **7** | [`07-system-architecture.md`](07-system-architecture.md) | 5-layer architecture, 9 microservices, technology stack, API design (70+ endpoints), deployment architecture (MVP + production), security architecture (5 defence layers), caching strategy, performance budgets |
| **8** | [`08-ieee-29148-srs.md`](08-ieee-29148-srs.md) | IEEE 29148-2018 compliant Software Requirements Specification: **63** functional requirements, **14** use cases, full traceability matrix, **12** end-to-end acceptance test scenarios |
| **9** | [`09-ux-specification.md`](09-ux-specification.md) | UX design for Customer App (11 screens), Shopper App (5 screens), Admin Dashboard (7 screens). Shared components, error states, loading states, empty states, 14 push notification templates |
| **10** | [`10-api-specification.md`](10-api-specification.md) | Complete REST API specification: ~80+ endpoints across 10 resource groups + 3 WebSocket channels. Request/response formats for every operation |
| **11** | [`11-database-design.md`](11-database-design.md) | 20 table definitions across 8 schemas. TimescaleDB hypertable for GPS data. Indexing strategy, partitioning, key query optimisation. Append-only audit log |
| **12** | [`12-implementation-roadmap.md`](12-implementation-roadmap.md) | 7 implementation phases (0-6) from foundation through V2. Key milestones, task breakdowns, team composition (9 → 20 people), risk register, dependency graph. Target: 20 weeks to hard launch |

---

## Supporting Documents

| Document | Description |
|----------|-------------|
| [`01-market-research.md`](01-market-research.md) | Global competitor analysis (10 platforms) + Tanzanian market research (mobile money, connectivity, competition, cultural factors) |
| [`05-release-scope.md`](05-release-scope.md) | MVP / V1 / V2 / Future scope breakdown with scope decision log |

---

## Key Statistics

| Metric | Value |
|--------|-------|
| Total business rules | **134** |
| Functional requirements | **63** |
| Non-functional requirements | **118** |
| Use cases | **14** |
| Domain entities | **22** |
| State machines | **4** |
| BPMN processes | **6** |
| API endpoints | **~80+** |
| Database tables | **20** |
| Design screens | **23** |
| Implementation phases | **7** |
| Team size (MVP) | **9** |

---

## Architecture at a Glance

```
┌─────────────┐  ┌─────────────┐  ┌──────────────────┐
│ Customer    │  │ Shopper     │  │ Admin Dashboard  │
│ App         │  │ App         │  │ (Web)            │
│ (Android/iOS)│  │ (Android/iOS)│  │                  │
└──────┬──────┘  └──────┬──────┘  └────────┬─────────┘
       │                │                   │
       └────────────────┼───────────────────┘
                        │
               ┌────────┴────────┐
               │   API Gateway   │
               └────────┬────────┘
                        │
          ┌─────────────┼─────────────┐
          │             │             │
    ┌─────┴─────┐ ┌────┴────┐ ┌─────┴─────┐
    │  User     │ │ Order   │ │ Assignment│
    │  Service  │ │ Service │ │ Engine    │
    └───────────┘ └─────────┘ └───────────┘
    ┌───────────┐ ┌─────────┐ ┌───────────┐
    │  Payment  │ │Delivery │ │  Dispute  │
    │  Service  │ │ Service │ │  Service  │
    └───────────┘ └─────────┘ └───────────┘
          │             │             │
          └─────────────┼─────────────┘
                        │
          ┌─────────────┴─────────────┐
          │      Message Broker       │
          │     (RabbitMQ/Kafka)      │
          └───────────────────────────┘
                        │
          ┌─────────────┼─────────────┐
          │             │             │
    ┌─────┴─────┐ ┌────┴────┐ ┌─────┴─────┐
    │PostgreSQL │ │ Redis  │ │ Timescale │
    │(primary)  │ │(cache) │ │ (GPS)     │
    └───────────┘ └─────────┘ └───────────┘
```

---

## Quick References

### MVP Features (Launch)

- Android customer + shopper apps
- Phone OTP registration
- Manual item entry with brand/unit/max price
- Multi-factor Assignment Score (C-011)
- 30-second offer window + 3-minute cascade
- Per-zone configurable delivery radius
- M-Pesa/Mixx payments (direct, no wallet)
- Stage-based shopper protection
- Cash on Delivery via Customer Trust Score
- Unified Dispute Resolution Framework
- Two-way ratings with blind period
- Admin dashboard (shopper vetting, disputes, metrics)
- Swahili + English language support

### V1 Features (Month 6-9)

- iOS apps
- Live map tracking
- Escrow integration
- Scheduled orders
- Promotions engine
- Shopper performance tiers
- PWA/USSD fallback
- Full Swahili localisation
- Support ticket system
- Enhanced fraud detection

### V2 Features (Month 12-18)

- Vendor integration portal
- Multi-shop orders
- Corporate/business accounts
- Loyalty programme
- Route optimization
- AI demand prediction

---

*This specification was produced through a systematic business analysis process following IEEE 29148-2018 standards. 134 business rules were defined and refined across 13 categories before any requirements were written. Total documented across 12 phases: ~5,500 lines.*

**Date:** 2026-07-24  
**Project:** Urban Shopper Platform — Tanzania
