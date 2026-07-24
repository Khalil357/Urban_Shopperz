# Urban Shopper Platform — Implementation Roadmap

> **Document Type:** Project Planning  
> **Status:** Complete  
> **Date:** 2026-07-24  
> **Version:** 1.0  
> **Cross-Reference:** All preceding phases (1-11)  
> **Phase:** 12 of 12

---

## Table of Contents

1. [Development Phases Overview](#1-development-phases-overview)
2. [Phase 0 — Foundation (Weeks 1-4)](#2-phase-0--foundation-weeks-1-4)
3. [Phase 1 — Core MVP (Weeks 5-12)](#3-phase-1--core-mvp-weeks-5-12)
4. [Phase 2 — MVP Launch (Weeks 13-20)](#4-phase-2--mvp-launch-weeks-13-20)
5. [Phase 3 — Stabilise & Iterate (Weeks 21-28)](#5-phase-3--stabilise--iterate-weeks-21-28)
6. [Phase 4 — V1 Features (Weeks 29-40)](#6-phase-4--v1-features-weeks-29-40)
7. [Phase 5 — Scale & Optimise (Weeks 41-52)](#7-phase-5--scale--optimise-weeks-41-52)
8. [Phase 6 — V2 Expansion (Months 13-18)](#8-phase-6--v2-expansion-months-13-18)
9. [Team Composition](#9-team-composition)
10. [Risk Register for Implementation](#10-risk-register-for-implementation)
11. [Dependency Graph](#11-dependency-graph)

---

## 1. Development Phases Overview

```
Month:    1   2   3   4   5   6   7   8   9  10  11  12  13-18
          ─── ─── ─── ─── ─── ─── ─── ─── ─── ─── ─── ─── ─────
Phase 0:  ████  FOUNDATION (Infrastructure, Auth, Dev Environment)
Phase 1:       ██████████  CORE MVP (Customer → Order → Payment)
Phase 2:             ██████████  MVP LAUNCH (Soft → Hard Launch)
Phase 3:                    ██████████  STABILISE (Fix, Optimise, iOS)
Phase 4:                          ████████████  V1 FEATURES
Phase 5:                                ████████████  SCALE
Phase 6:                                              ██████████  V2 EXPANSION
```

### Key Milestones

| Milestone | Target | Deliverable |
|-----------|--------|-------------|
| M0 | Week 4 | Dev environment ready. CI/CD pipeline. Database schema deployed. Auth working. |
| M1 | Week 8 | Customer can register, create order, payment pre-auth, assignment run |
| M2 | Week 12 | Complete order loop: customer → shopper → delivery → payment → rating |
| M3 | Week 16 | Soft launch: limited zones, invited users, monitored operations |
| M4 | Week 20 | Hard launch: all launch zones, public marketing, referral programme live |
| M5 | Week 28 | iOS apps live. Post-launch bugs fixed. Performance optimised. |
| M6 | Week 40 | V1 features: escrow, scheduled orders, promotions, support system |
| M7 | Week 52 | Year 1: stable operations. Multi-city expansion planned. |
| M8 | Month 18 | V2 features: vendor portal, multi-shop, corporate accounts, loyalty |

---

## 2. Phase 0 — Foundation (Weeks 1-4)

**Objective:** Set up the development foundation before any feature work begins.

### Week 1-2: Infrastructure

| Task | Owner | Dependencies | Duration |
|------|-------|--------------|----------|
| Set up cloud account (AWS/GCP/Azure) | DevOps | None | 1 day |
| Configure VPC, subnets, security groups | DevOps | Cloud account | 2 days |
| Deploy PostgreSQL 16+ with TimescaleDB | DevOps | VPC | 2 days |
| Deploy Redis cluster | DevOps | VPC | 1 day |
| Set up CI/CD pipeline (GitHub Actions) | DevOps | Repo | 2 days |
| Configure monitoring stack (Grafana/Prometheus) | DevOps | Infrastructure | 2 days |
| Set up object storage (S3/GCS) | DevOps | Cloud account | 1 day |
| Configure CDN | DevOps | Cloud account | 1 day |

### Week 3-4: Core Services Skeleton

| Task | Owner | Dependencies | Duration |
|------|-------|--------------|----------|
| API Gateway setup (Kong/API GW) | Backend | Infrastructure | 3 days |
| User service: auth + OTP + JWT | Backend | Database | 5 days |
| User service: customer CRUD | Backend | Auth | 3 days |
| User service: shopper CRUD | Backend | Auth | 3 days |
| Database schema deployment (migrations) | Backend | Database | 3 days |
| Mobile app project scaffolding (Android) | Mobile | None | 3 days |
| Admin dashboard project scaffolding (Web) | Frontend | None | 2 days |
| Shared component library (UI kit) | Frontend | None | 3 days |

**Phase 0 Exit Criteria:**
- [ ] Developer can run full stack locally
- [ ] CI/CD pipeline builds and deploys to staging
- [ ] API Gateway returns health check
- [ ] User can register and authenticate via OTP
- [ ] Database migrations run cleanly
- [ ] Mobile app builds and runs on physical device

---

## 3. Phase 1 — Core MVP (Weeks 5-12)

**Objective:** Build the complete order loop — from customer registration through delivery and payment.

### Week 5-6: Customer App — Registration & Order Creation

| Task | Owner | Dependencies | Duration |
|------|-------|--------------|----------|
| Customer app: onboarding screens (3) | Mobile | Phase 0 | 3 days |
| Customer app: home screen + navigation | Mobile | Phase 0 | 2 days |
| Customer app: order creation (location + items) | Mobile | User service | 4 days |
| Customer app: price display + submission | Mobile | Order service | 3 days |
| Order service: create order endpoint | Backend | User service | 3 days |
| Order service: state machine engine | Backend | Database | 4 days |

### Week 7-8: Assignment Engine

| Task | Owner | Dependencies | Duration |
|------|-------|--------------|----------|
| Assignment engine: GPS ping ingestion | Backend | Infrastructure | 4 days |
| Assignment engine: Haversine + Road distance | Backend | Maps API key | 3 days |
| Assignment engine: Assignment Score calc | Backend | User service | 3 days |
| Assignment engine: offer cascade (30s + 3min) | Backend | GPS service | 4 days |
| Shopper app: Go Online/Offline + GPS | Mobile | Phase 0 | 3 days |
| Shopper app: offer countdown screen | Mobile | Assignment engine | 3 days |

### Week 9-10: Shopping & Delivery Flow

| Task | Owner | Dependencies | Duration |
|------|-------|--------------|----------|
| Shopper app: accept order + travel screen | Mobile | Assignment engine | 2 days |
| Shopper app: item list + status tracking | Mobile | Order service | 4 days |
| Shopper app: receipt upload (multi-format) | Mobile | Infrastructure | 3 days |
| Shopper app: delivery screen + photo | Mobile | GPS service | 3 days |
| Shopper app: chat implementation | Mobile | Notification svc | 4 days |
| Customer app: order tracking screen | Mobile | Order service | 3 days |
| Customer app: chat implementation | Mobile | Notification svc | 2 days |
| Delivery service: ETA calculation | Backend | Maps API | 3 days |

### Week 11-12: Payment & Rating

| Task | Owner | Dependencies | Duration |
|------|-------|--------------|----------|
| M-Pesa API integration (pre-auth, capture, refund) | Backend | Payment provider | 5 days |
| Mixx API integration | Backend | Payment provider | 4 days |
| Payment service: shopper payout (48h) | Backend | Order service | 3 days |
| Shopper wallet implementation | Backend | Payment service | 3 days |
| Customer app: rate shopper screen | Mobile | Phase 1 | 2 days |
| Shopper app: earnings dashboard | Mobile | Payment service | 3 days |
| Shopper app: withdrawal flow | Mobile | Payment service | 2 days |

**Phase 1 Exit Criteria (M2 Milestone):**
- [ ] Customer can register, create order, see price, submit
- [ ] Order transitions through full state machine
- [ ] Shopper can go online, receive offer, accept, shop, upload receipt, deliver
- [ ] Payment pre-auth + capture works end-to-end with M-Pesa
- [ ] Customer can rate shopper (blind period respected)
- [ ] Shopper receives payout after 48-hour settlement
- [ ] All states logged and auditable

---

## 4. Phase 2 — MVP Launch (Weeks 13-20)

**Objective:** Prepare for public launch. Admin tools, fraud detection, quality assurance.

### Week 13-14: Admin Dashboard

| Task | Owner | Dependencies | Duration |
|------|-------|--------------|----------|
| Admin dashboard: overview metrics | Frontend | Phase 1 | 3 days |
| Admin dashboard: shopper management | Frontend | User service | 3 days |
| Admin dashboard: order management | Frontend | Order service | 3 days |
| Admin dashboard: basic dispute resolution | Frontend | — | 4 days |
| Admin dashboard: zone management | Frontend | Assignment engine | 2 days |
| Admin auth + RBAC (3 roles) | Backend | User service | 2 days |

### Week 15-16: Fraud Detection & Admin Features

| Task | Owner | Dependencies | Duration |
|------|-------|--------------|----------|
| Duplicate account detection | Backend | User service | 2 days |
| Transaction velocity checks | Backend | Order service | 2 days |
| Prohibited items filter | Backend | Order service | 2 days |
| Receipt price verification (baseline) | Backend | Order service | 3 days |
| GPS spoofing detection | Backend | GPS service | 3 days |
| Account takeover prevention | Backend | Auth | 2 days |

### Week 17-18: Operational Readiness

| Task | Owner | Dependencies | Duration |
|------|-------|--------------|----------|
| Load testing (target: 10x MVP load) | DevOps | All services | 4 days |
| Performance optimisation | Backend | Load test results | 3 days |
| Security audit + penetration test | Security | Infrastructure | 4 days |
| Backup + DR testing | DevOps | Infrastructure | 2 days |
| SLA monitoring setup | DevOps | Infrastructure | 2 days |
| Error tracking (Sentry) integration | Backend | All apps | 2 days |

### Week 19-20: Soft Launch + Hard Launch

| Task | Owner | Dependencies | Duration |
|------|-------|--------------|----------|
| Soft launch in 2 zones (Mikocheni, Masaki) | Ops | Readiness | 2 weeks |
| Bug triage + critical fixes | All | Soft launch | Ongoing |
| Shopper recruitment campaign | Ops | None | Weeks 17-20 |
| Hard launch — all launch zones | Ops | Soft launch pass | Week 20 |
| Marketing campaign launch | Marketing | Hard launch | Week 20 |

**Phase 2 Exit Criteria (M4 Milestone):**
- [ ] Soft launch completed with 100+ successful orders
- [ ] All critical bugs fixed
- [ ] Fraud detection running in production
- [ ] Admin team trained and using dashboard
- [ ] Load test passes (10x target)
- [ ] Security audit passed
- [ ] 500+ active shoppers registered
- [ ] 10,000+ customers registered
- [ ] 95%+ order fulfillment rate

---

## 5. Phase 3 — Stabilise & Iterate (Weeks 21-28)

**Objective:** Fix post-launch issues, add iOS, improve based on real usage data.

### Weeks 21-24: Post-Launch Fixes

| Task | Owner | Dependencies | Duration |
|------|-------|--------------|----------|
| Root cause analysis of top 10 issues | All | Production data | 2 weeks |
| Critical bug fixes | Backend/Mobile | RCA | Ongoing |
| Performance tuning based on real load | Backend | Production data | 2 weeks |
| Mobile money API reliability improvements | Backend | Payment logs | 1 week |
| Chat reliability improvements | Backend | Production data | 1 week |

### Weeks 25-28: Polish & V1 Prep

| Task | Owner | Dependencies | Duration |
|------|-------|--------------|----------|
| Flutter app polish (both platforms) | Mobile | Phase 1 | 4 weeks |
| PWA basic version (feature phone order) | Frontend | Phase 1 API | 3 weeks |
| Data-driven SLA tuning | Backend | 4+ weeks production data | 1 week |

**Phase 3 Exit Criteria:**
- [ ] Error rate < 1% of all orders
- [ ] 99.5% uptime achieved over 30 days
- [ ] iOS apps submitted to App Store
- [ ] All SRS acceptance criteria passing

---

## 6. Phase 4 — V1 Features (Weeks 29-40)

**Objective:** Add scheduled orders, promotions, escrow, support system, enhanced reporting.

### Weeks 29-32: Escrow + Scheduled Orders

| Task | Owner | Dependencies | Duration |
|------|-------|--------------|----------|
| Escrow integration or trust account setup | Backend | Legal review | 3 weeks |
| Scheduled order release engine | Backend | Order service | 2 weeks |
| Customer app: scheduled order UI | Mobile | Scheduled engine | 2 weeks |
| Shopper app: scheduled order notifications | Mobile | Scheduled engine | 1 week |

### Weeks 33-36: Promotions + Support

| Task | Owner | Dependencies | Duration |
|------|-------|--------------|----------|
| Promotions engine (admin-configurable codes) | Backend | Order service | 2 weeks |
| Customer app: promo code entry + display | Mobile | Promotions engine | 1 week |
| Support ticket system | Backend | — | 2 weeks |
| Admin dashboard: support queue | Frontend | Support system | 1 week |
| Admin dashboard: enhanced reporting | Frontend | Analytics DB | 2 weeks |

### Weeks 37-40: Multi-Language + Advanced Admin

| Task | Owner | Dependencies | Duration |
|------|-------|--------------|----------|
| Full Swahili localisation (all apps) | All | i18n framework | 3 weeks |
| Admin dashboard: fraud dashboard | Frontend | Fraud detection | 2 weeks |
| Admin dashboard: SLA monitoring | Frontend | Monitoring | 1 week |
| Performance tier engine (shopper) | Backend | User service | 2 weeks |
| Customer app: map-based tracking (V1) | Mobile | Delivery service | 2 weeks |

**Phase 4 Exit Criteria (M6 Milestone):**
- [ ] Scheduled orders working end-to-end
- [ ] Escrow active with legal sign-off
- [ ] Promotions engine live with 3 active campaigns
- [ ] Support ticket system handling 90% of inbound
- [ ] Full Swahili localisation complete
- [ ] Shopper performance tiers calculated and active

---

## 7. Phase 5 — Scale & Optimise (Weeks 41-52)

**Objective:** Scale to multi-city, optimise costs, strengthen data moat.

| Task | Owner | Dependencies | Duration |
|------|-------|--------------|----------|
| Arusha zone setup + local recruitment | Ops | Phase 4 | 4 weeks |
| Mwanza zone setup + local recruitment | Ops | Phase 4 | 4 weeks |
| Database read replicas + query optimisation | Backend | — | 2 weeks |
| Caching improvements (Redis) | Backend | — | 2 weeks |
| Unit economics optimisation | Ops/Backend | 6+ months data | Ongoing |
| Advanced fraud detection (ML patterns) | Backend/ML | 50K+ orders data | 4 weeks |

**Phase 5 Exit Criteria (M7 Milestone):**
- [ ] 3 cities operational (Dar, Arusha, Mwanza)
- [ ] 100,000 orders/month processed
- [ ] Positive contribution margin per order
- [ ] 99.5%+ uptime sustained

---

## 8. Phase 6 — V2 Expansion (Months 13-18)

**Objective:** Build moats — vendor portal, multi-shop, corporate accounts, loyalty.

| Feature | Team | Dependencies | Estimated Duration |
|---------|------|--------------|-------------------|
| Vendor integration portal | Full-stack | Phase 5 | 8 weeks |
| Vendor inventory API | Backend | Vendor portal | 6 weeks |
| Multi-shop orders | Backend/Mobile | Vendor portal | 8 weeks |
| Corporate accounts | Backend/Frontend | — | 6 weeks |
| Loyalty programme (points + tiers) | Backend/Mobile | — | 5 weeks |
| Route optimisation (batch delivery) | Backend/ML | — | 8 weeks |
| AI demand prediction (admin) | Data Science | 6+ months data | 6 weeks |

---

## 9. Team Composition

### MVP Phase (Months 1-5)

| Role | Count | Focus |
|------|-------|-------|
| Backend Developer (Java/Spring Boot) | 2 | API services, assignment engine, payments |
| Mobile Developer (Flutter) | 2 | Customer app, shopper app (cross-platform) |
| Frontend Developer (Next.js/TypeScript) | 1 | Admin dashboard |
| DevOps Engineer | 1 | Infrastructure, CI/CD, monitoring |
| UI/UX Designer | 1 | App design, prototypes, user testing |
| Product Manager | 1 | Requirements, prioritisation, stakeholder mgmt |
| QA Engineer | 1 | Testing, automation, acceptance criteria |
| **Total** | **9** | |

### Post-Launch (Months 6-12)

| Role | Count | New Focus |
|------|-------|-----------|
| Mobile Developer (Flutter) +1 | 3 | Additional features across both platforms |
| Backend Developer +1 | 3 | Fraud detection, optimisation, V1 features |
| Data Analyst | 1 | Metrics, fraud pattern analysis, optimisation |
| Customer Support Lead | 1 | Build support team, dispute workflows |
| **Total** | **13** | |

### V2 Phase (Months 13+)

| Role | Count | New Focus |
|------|-------|-----------|
| Data Scientist/ML Engineer | 1 | Demand prediction, route optimisation |
| Additional Backend | 4 | Vendor portal, multi-shop, corporate accounts |
| Additional Mobile (Flutter) | 3 | V2 features across both platforms |
| **Total** | **~20** | |

---

## 10. Risk Register for Implementation

| Risk | Likelihood | Impact | Mitigation | Owner |
|------|:----------:|:------:|------------|-------|
| M-Pesa API integration delays or instability | Medium | Critical | Mixx as parallel provider. Abstraction layer to swap providers. Build retry + fallback logic early. | Backend Lead |
| Difficulty recruiting shoppers for supply | High | High | Pre-launch recruitment campaign. Signup bonuses. Boda-boda stand partnerships. Referral incentives. | Ops Lead |
| Mobile app rejected by Play Store | Low | High | Pre-submit to Play Store policy review. Ensure compliance with financial services policies. Have legal prepare documentation. | Mobile Lead |
| Performance issues at launch (unexpected load) | Medium | High | Load test to 10x target. Auto-scaling configured. Performance budget monitoring. Feature flags to disable non-critical features. | DevOps |
| Regulatory intervention on worker classification | Low | High | Legal counsel retainer. Engagement with TCRA/MIT from Day 1. Architecture supports both contractor and employee models. | Product/CEO |
| Currency volatility affecting operating costs | Medium | Medium | Local revenue = local costs. Minimise USD-denominated expenses. | CFO/CEO |
| Founders / team burnout | Medium | High | Sustainable pace (no sustained > 50h weeks). Clear prioritisation. Phase-based scope guardrails. | CEO |

---

## 11. Dependency Graph

```
Phase 0: Infrastructure
    ├── Phase 1: Core MVP
    │   ├── Customer Registration ──┐
    │   ├── Order Creation ─────────┤
    │   ├── Assignment Engine ──────┤
    │   ├── Shopping Workflow ──────┤
    │   ├── Delivery Workflow ──────┤
    │   ├── Payment Integration ────┤
    │   └── Rating System ──────────┘
    │
    ├── Phase 2: MVP Launch
    │   ├── Admin Dashboard
    │   ├── Fraud Detection
    │   ├── Load Testing
    │   ├── Security Audit
    │   ├── Soft Launch
    │   └── Hard Launch
    │
    ├── Phase 3: Stabilise
    │   ├── Bug Fixes
    │   ├── iOS Apps
    │   └── PWA
    │
    ├── Phase 4: V1 Features
    │   ├── Escrow Integration
    │   ├── Scheduled Orders
    │   ├── Promotions Engine
    │   ├── Support System
    │   └── Multi-Language
    │
    ├── Phase 5: Scale
    │   ├── Multi-City (Arusha, Mwanza)
    │   └── Performance Optimisation
    │
    └── Phase 6: V2
        ├── Vendor Portal
        ├── Multi-Shop Orders
        ├── Corporate Accounts
        ├── Loyalty Programme
        └── AI Features
```

---

*This document is Phase 12 — the final phase of the Urban Shopper Platform specification. It defines the complete implementation roadmap from foundation through V2 expansion, covering team, timeline, risks, and dependencies.*
