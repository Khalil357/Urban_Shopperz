# Urban Shopper Platform — System Architecture

> **Document Type:** Technical Architecture  
> **Status:** Complete  
> **Date:** 2026-07-24  
> **Version:** 1.0  
> **Cross-Reference:** Domain Model — `02-domain-model.md`, State Machines — `04-state-machines.md`, NFRs — `06-non-functional-requirements.md`  
> **Phase:** 7 of 12

---

## Table of Contents

1. [Architecture Principles](#1-architecture-principles)
2. [High-Level Architecture](#2-high-level-architecture)
3. [Component Decomposition](#3-component-decomposition)
4. [Technology Stack](#4-technology-stack)
5. [API Design](#5-api-design)
6. [Data Architecture](#6-data-architecture)
7. [Deployment Architecture](#7-deployment-architecture)
8. [Security Architecture](#8-security-architecture)
9. [Integration Architecture](#9-integration-architecture)
10. [Scalability & Performance Design](#10-scalability--performance-design)

---

## 1. Architecture Principles

| # | Principle | Rationale |
|---|-----------|-----------|
| P1 | **Microservices-dominant architecture** — Core business capabilities (assignment, order, payment, notification) are independent services communicating via asynchronous messaging and REST APIs. | Enables independent scaling, deployment, and team ownership |
| P2 | **API Gateway as single entry point** — All client requests route through an API gateway that handles authentication, rate limiting, routing, and request transformation. | Security, observability, and protocol abstraction |
| P3 | **Event-driven for real-time flows** — Assignment, GPS ingestion, notifications, and order state transitions use an event-driven pattern with a message broker. | Real-time responsiveness without blocking |
| P4 | **Command Query Responsibility Segregation (CQRS)** — Write operations use relational (SQL) models; read/analytics use optimised read models and caches. | Write performance and read scalability optimised independently |
| P5 | **Stateless services** — Application services store no session state in memory. Session state is in Redis; authentication state is in JWTs. | Horizontal scaling without affinity requirements |
| P6 | **Provider abstraction** — Every third-party integration (M-Pesa, SMS, Maps, Push Notifications) is behind an interface/contract. | Swappable providers without code changes |
| P7 | **Defence in depth** — Security at every layer: network, transport, application, data. | Protection against the multi-vector threat environment |
| P8 | **Offline resilience by design** — Mobile apps queue operations locally and sync when connectivity returns. | Tanzanian network reality |

---

## 2. High-Level Architecture

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│                            CLIENT LAYER                                           │
│                                                                                   │
│  ┌────────────────────┐  ┌────────────────────┐  ┌───────────────────────────┐  │
│  │  Customer App      │  │  Shopper App       │  │  Admin Dashboard (Web)    │  │
│  │  (Android / iOS)   │  │  (Android / iOS)   │  │  (React / Vue / Angular) │  │
│  └─────────┬──────────┘  └─────────┬──────────┘  └─────────────┬─────────────┘  │
│            │                       │                            │                 │
└────────────┼───────────────────────┼────────────────────────────┼─────────────────┘
             │                       │                            │
             │         HTTPS / WSS   │        HTTPS               │
             ▼                       ▼                            ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                            EDGE LAYER                                           │
│                                                                                   │
│  ┌────────────────────────────────────────────────────────────────────────┐      │
│  │                    API Gateway (Kong / AWS API Gateway)                │      │
│  │  · Authentication & Authorisation (JWT validation)                     │      │
│  │  · Rate limiting · Request/Response transformation · Request logging   │      │
│  │  · TLS termination · IP whitelisting for admin dashboard               │      │
│  └────────────────────────────────────────────────────────────────────────┘      │
│  ┌─────────────────────────────┐  ┌──────────────────────────────────────┐       │
│  │  CDN (CloudFront / Cloudflare) │  │  Load Balancer (ALB / NLB)         │       │
│  └─────────────────────────────┘  └──────────────────────────────────────┘       │
└──────────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                         APPLICATION LAYER (Microservices)                       │
│                                                                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌─────────────────────┐  │
│  │  User Service│  │  Order       │  │  Assignment  │  │  Payment Service    │  │
│  │  · Customer  │  │  Service     │  │  Engine      │  │  · Pre-auth         │  │
│  │  · Shopper   │  │  · CRUD      │  │  · Score     │  │  · Capture          │  │
│  │  · Admin     │  │  · Lifecycle │  │  · Cascade   │  │  · Payout           │  │
│  │  · Auth      │  │  · Items     │  │  · Zones     │  │  · Refund           │  │
│  │  · Verification│  · Receipts  │  │               │  └─────────────────────┘  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘     ┌─────────────────────┐  │
│         │                 │                 │             │  Dispute Service    │  │
│  ┌──────┴───────┐  ┌──────┴───────┐  ┌──────┴───────┐  │  · Resolution        │  │
│  │ Notification │  │  Delivery    │  │  Rating      │  │  · Evidence          │  │
│  │  Service     │  │  Service     │  │  Service     │  │  · Compensation      │  │
│  │  · Push      │  │  · Tracking  │  │  · Submit    │  └─────────────────────┘  │
│  │  · SMS       │  │  · ETA       │  │  · Aggregate │                            │
│  │  · In-App    │  │  · Proof     │  │  · FraudDet  │                            │
│  └──────────────┘  └──────────────┘  └──────────────┘                            │
│                                                                                   │
│  ┌──────────────────────────────────────────────────────────────────────────┐    │
│  │                    Message Broker (RabbitMQ / Kafka)                      │    │
│  │  · Order events · Payment events · GPS events · Notification events      │    │
│  │  · Assignment events · Dispute events                                    │    │
│  └──────────────────────────────────────────────────────────────────────────┘    │
└──────────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                            DATA LAYER                                            │
│                                                                                   │
│  ┌──────────────────┐  ┌──────────────────┐  ┌───────────────────────────────┐   │
│  │  Primary DB      │  │  Cache           │  │  Search / Analytics Engine    │   │
│  │  (PostgreSQL)    │  │  (Redis)         │  │  (Elasticsearch / OpenSearch) │   │
│  │  · Users         │  │  · Sessions      │  │  · Order search               │   │
│  │  · Orders        │  │  · GPS positions │  │  · Fraud pattern analysis     │   │
│  │  · Payments      │  │  · Rate limits   │  │  · Audit log indexing         │   │
│  │  · Transactions  │  │  · Shopping cart │  │  · Shopper performance        │   │
│  │  · Disputes      │  │  · Offer state   │  │                               │   │
│  │  · Ratings       │  │  · Feature flags │  │  ┌────────────────────────────┐│   │
│  │  · Notifications │  └──────────────────┘  │  │  Object / File Storage     ││   │
│  │  · Audit logs    │                       │  │  (S3 / GCS / Azure Blob)    ││   │
│  └──────────────────┘                       │  │  · Receipt photos           ││   │
│                                              │  │  · ID document scans        ││   │
│                                              │  │  · Delivery photos          ││   │
│                                              │  └────────────────────────────┘│   │
│  ┌──────────────────┐                       └───────────────────────────────┘   │
│  │  GPS Data Store  │                                                           │
│  │  (TimescaleDB    │                                                           │
│  │  / InfluxDB)     │                                                           │
│  │  · Shopper GPS   │                                                           │
│  │  · 30-day retention│                                                        │
│  └──────────────────┘                                                           │
└──────────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                        EXTERNAL INTEGRATION LAYER                               │
│                                                                                   │
│  ┌───────────┐  ┌───────────┐  ┌───────────┐  ┌───────────┐  ┌───────────────┐  │
│  │  M-Pesa   │  │  Mixx     │  │  Airtel   │  │  SMS      │  │  Maps         │  │
│  │  API      │  │  API      │  │  Money    │  │  Gateway  │  │  API          │  │
│  └───────────┘  └───────────┘  └───────────┘  └───────────┘  └───────────────┘  │
│                                               ┌───────────┐  ┌───────────────┐  │
│                                               │  Firebase  │  │  NIDA (future)│  │
│                                               │  Cloud     │  │  Identity    │  │
│                                               │  Messaging │  │  API         │  │
│                                               └───────────┘  └───────────────┘  │
└──────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Component Decomposition

### 3.1 Service Descriptions

| Service | Responsibility | Key Capabilities | State Machine(s) Owned |
|---------|---------------|------------------|------------------------|
| **User Service** | Customer, Shopper, Admin account management. Authentication, authorisation, verification. | Registration (OTP), login, profile management, identity verification (ID upload + facial recognition), shopper onboarding, document management, role-based access control | Shopper lifecycle (Pending → Active → Suspended) |
| **Order Service** | Order lifecycle management. Item tracking. Receipt management. | Order CRUD, status transitions (D-004 state machine), item tracking (Found/Substituted/Not Available), receipt upload and validation, order history, order archival | Order State Machine |
| **Assignment Engine** | GPS-based shopper matching. Offer cascade. Zone management. | GPS ping ingestion (5-30s frequency), Haversine + Road distance calculation, Assignment Score calculation (C-011), offer dispatch, cascade management, zone configuration | Shopper Work State Machine (Online → Assigned → ... → Available) |
| **Payment Service** | Financial transactions. Mobile money integration. Wallet management. | Pre-auth hold, payment capture, refund processing, 48-hour shopper settlement, tiered fee calculation (F-001, F-005), shopper wallet balance management, financial audit logging | Payment State Machine |
| **Delivery Service** | Delivery tracking, ETA calculation, proof of delivery. | GPS route tracking, dynamic ETA calculation (E-003), ETA recalculation (E-009), delivery confirmation, proof of delivery photo management, inspection window management | — |
| **Dispute Service** | Unified dispute resolution across all issue types. | Dispute triage (L-008), evidence collection, automated resolution, manual review workflow, compensation execution, escalation management | Dispute State Machine |
| **Rating Service** | Rating submission, calculation, fraud detection. | Rating submission (blind period), recency-weighted average calculation, rating fraud detection (H-005), Trust Score computation (shopper + customer) | — |
| **Notification Service** | Multi-channel notification delivery. | Push notification via FCM, SMS via gateway, in-app notification, template management, delivery status tracking, language-localised sending | Notification lifecycle |
| **GPS Ingestion Service** | High-throughput GPS ping reception and processing. | Receive GPS pings from shopper app at tiered frequencies (C-001), store for 30-day retention (L-003), publish to Kafka/Redis for Assignment Engine and Delivery Service, spoofing detection (C-010) | — |

### 3.2 Supporting Services

| Service | Responsibility |
|---------|---------------|
| **API Gateway** | Single entry point. TLS termination. JWT validation. Rate limiting. Request logging. Request transformation. Routing to appropriate microservice. |
| **Message Broker** | Asynchronous event bus. Decouples service communication. Guarantees at-least-once delivery. Topics per domain: `order.events`, `payment.events`, `assignment.events`, `gps.pings`, `notification.events`, `dispute.events`. |
| **Scheduler / Cron Service** | Triggers time-based events: 48-hour settlement, 90-day order archival, dormant account detection, scheduled order release (D-014). |
| **Fraud Detection Service** | Background analysis of fraud patterns: velocity checks, mule detection, duplicate account detection, rating fraud analysis. Consumes events from message broker. |

### 3.3 Internal Communication Patterns

| Pattern | Protocol | Use Cases |
|---------|----------|-----------|
| **Synchronous (Request-Response)** | REST/HTTPS (JSON) | CRUD operations, order creation, user registration, payment capture |
| **Asynchronous (Event-Driven)** | Message Broker (AMQP / Kafka) | GPS pings, order state transitions, payment confirmations, notification triggers, assignment events |
| **Real-Time (Bidirectional)** | WebSocket (WSS) | Live GPS tracking to customer, offer countdown timing, live admin dashboard updates |
| **Background Job** | Worker Queue | 48-hour settlement batch, order archival, dormant account cleanup, ETA recalculation sweeps |

---

## 4. Technology Stack

### 4.1 Recommended Stack

| Layer | Technology | Rationale |
|-------|------------|-----------|
| **Customer App** | Flutter | Cross-platform (Android + iOS) from a single codebase. Single development team. Fast iteration. |
| **Shopper App** | Flutter | Same codebase as customer app with feature-flag based role separation. Cross-platform GPS and camera support. |
| **Admin Dashboard** | Next.js + TypeScript | Full-stack React framework. Server-side rendering for dashboard performance. TypeScript for type safety. |
| **Backend Services** | Spring Boot 3.x + Java 21 | Mature ecosystem, strong typing, excellent performance. Spring Security for auth. Extensive community and library support. |
| **ORM** | Spring Data JPA + Hibernate | Proven object-relational mapping. Seamless Spring Boot integration. |
| **Database Migration** | Flyway | Version-controlled database migrations. Integrates with Spring Boot. Rollback support. |
| **Primary Database** | PostgreSQL + PostGIS | PostGIS for geospatial queries (distance calculation, zone boundary containment). JSONB for flexible fields. Strong reliability and performance. |
| **Cache** | Redis | Session storage, GPS positions for active shoppers, rate limiting counters, feature flags, real-time offer state, Assignment Score caching |
| **Messaging** | RabbitMQ | Reliable message broker for service orchestration. Supports at-least-once delivery. Well-suited for order event processing and notification dispatch. |
| **Object Storage** | S3-compatible (MinIO, Cloudflare R2, or Amazon S3) | Receipt photos, ID document scans, delivery photos. Vendor-agnostic via S3 API compatibility. |
| **Maps** | Google Maps Platform | Road distance calculation, geocoding, route estimation, map display. Industry-standard for delivery platforms. |
| **Push Notifications** | Firebase Cloud Messaging (FCM) | Reliable push delivery to Android and iOS. SMS gateway as fallback. |
| **Payments** | M-Pesa + Mixx (adapter-based design) | Provider abstraction interface. Easy to add Airtel Money or new providers. Configuration-based provider selection. |
| **Authentication** | Spring Security + JWT | Industry-standard security framework. JWT for stateless authentication. Device binding support. |
| **Monitoring** | Prometheus + Grafana + Spring Boot Actuator | Prometheus for metrics collection. Grafana for dashboards. Actuator for health checks and application metrics. |
| **Logging** | Loki or ELK Stack (Elasticsearch, Logstash, Kibana) | Centralised log aggregation. Loki for lightweight Kubernetes-native logging. ELK for advanced search and analytics. |
| **Error Tracking** | Sentry | Real-time error tracking and performance monitoring. Supports Flutter, Next.js, and Spring Boot. |
| **CI/CD** | GitHub Actions | Build, test, lint, security scan, deploy. Integrated with GitHub repository. |
| **Deployment** | Docker Compose + Nginx | Docker Compose for MVP deployment. Nginx as reverse proxy and load balancer. Containerised services for consistency across environments. |

### 4.2 Database Per Service Strategy

Each microservice owns its data and exposes it only through its API:

```
┌─────────────────────┐
│   PostgreSQL         │
│   (Shared instance   │
│    with per-service  │
│    schemas for MVP,  │
│    separate DBs at   │
│    scale)            │
│                     │
│  ┌─────────────────┐│
│  │ user_schema     ││  ← User Service
│  ├─────────────────┤│
│  │ order_schema    ││  ← Order Service
│  ├─────────────────┤│
│  │ payment_schema  ││  ← Payment Service
│  ├─────────────────┤│
│  │ dispute_schema  ││  ← Dispute Service
│  ├─────────────────┤│
│  │ rating_schema   ││  ← Rating Service
│  ├─────────────────┤│
│  │ delivery_schema ││  ← Delivery Service
│  ├─────────────────┤│
│  │ audit_schema    ││  ← Audit Logging (service-agnostic)
│  └─────────────────┘│
└─────────────────────┘

┌─────────────────────┐
│  TimescaleDB         │
│  ┌─────────────────┐│
│  │ gps_data        ││  ← GPS Ingestion Service
│  └─────────────────┘│
└─────────────────────┘

┌─────────────────────┐
│  Redis               │
│  ┌─────────────────┐│
│  │ sessions        ││  ← API Gateway / User Service
│  │ active_shoppers ││  ← Assignment Engine
│  │ rate_limits     ││  ← API Gateway
│  │ feature_flags   ││  ← Admin (config)
│  │ offer_state     ││  ← Assignment Engine (30s offers)
│  └─────────────────┘│
└─────────────────────┘
```

---

## 5. API Design

### 5.1 API Principles

| Principle | Detail |
|-----------|--------|
| RESTful | Resource-oriented endpoints. HTTP verbs: GET (read), POST (create), PUT (update), PATCH (partial update), DELETE (remove) |
| Versioned | `/api/v1/orders`, `/api/v1/shoppers` — backward compatibility for 2 minor versions |
| Consistent | Snake_case JSON fields. ISO 8601 timestamps. UUIDs for resource IDs. Standard pagination (`?page=1&per_page=20`) |
| Authenticated | JWT Bearer tokens in `Authorization` header. Admin endpoints additionally require API key for server-to-server calls. |
| Rate Limited | 100 req/min per user (standard), 30 req/min for auth endpoints, 10 req/min for OTP. Admin: 300 req/min |
| Documented | OpenAPI 3.0 specification. Public API reference. |

### 5.2 Core API Endpoints

```
# Customer API
POST   /api/v1/customers/register
POST   /api/v1/customers/verify-otp
GET    /api/v1/customers/{id}/profile
PATCH  /api/v1/customers/{id}/profile
POST   /api/v1/customers/{id}/deactivate
GET    /api/v1/customers/{id}/orders
GET    /api/v1/customers/{id}/orders/{orderId}
GET    /api/v1/customers/{id}/wallet           (V1)

# Order API
POST   /api/v1/orders
GET    /api/v1/orders/{id}
PATCH  /api/v1/orders/{id}/cancel
GET    /api/v1/orders/{id}/status
GET    /api/v1/orders/{id}/items
POST   /api/v1/orders/{id}/items/{itemId}/status
POST   /api/v1/orders/{id}/receipt
GET    /api/v1/orders/{id}/tracking

# Shopper API
POST   /api/v1/shoppers/register
POST   /api/v1/shoppers/verify-identity
GET    /api/v1/shoppers/{id}/profile
PATCH  /api/v1/shoppers/{id}/availability
GET    /api/v1/shoppers/{id}/offers
POST   /api/v1/shoppers/{id}/offers/{offerId}/accept
POST   /api/v1/shoppers/{id}/offers/{offerId}/decline
GET    /api/v1/shoppers/{id}/active-order
GET    /api/v1/shoppers/{id}/wallet
POST   /api/v1/shoppers/{id}/wallet/withdraw
GET    /api/v1/shoppers/{id}/earnings

# Assignment Engine (Internal)
POST   /api/v1/assignments/calculate-score       (Internal)
POST   /api/v1/assignments/offer                 (Internal)
POST   /api/v1/assignments/cascade-timeout       (Internal)

# Delivery API
POST   /api/v1/deliveries/{id}/arrive
POST   /api/v1/deliveries/{id}/confirm
POST   /api/v1/deliveries/{id}/report-delay
GET    /api/v1/deliveries/{id}/eta
GET    /api/v1/deliveries/{id}/route              (V1)

# Payment API
POST   /api/v1/payments/pre-auth
POST   /api/v1/payments/capture
POST   /api/v1/payments/refund
GET    /api/v1/payments/{id}/status
GET    /api/v1/payments/{id}/transactions

# Dispute API
POST   /api/v1/disputes
GET    /api/v1/disputes/{id}
POST   /api/v1/disputes/{id}/evidence
POST   /api/v1/disputes/{id}/escalate
GET    /api/v1/disputes/{id}/resolution

# Rating API
POST   /api/v1/ratings
GET    /api/v1/shoppers/{id}/ratings
GET    /api/v1/customers/{id}/ratings            (Internal — for Trust Score)

# Admin API
GET    /api/v1/admin/metrics
GET    /api/v1/admin/shoppers/pending
POST   /api/v1/admin/shoppers/{id}/approve
POST   /api/v1/admin/shoppers/{id}/reject
GET    /api/v1/admin/disputes/queue
POST   /api/v1/admin/disputes/{id}/resolve
GET    /api/v1/admin/fraud/alerts
POST   /api/v1/admin/zones
PATCH  /api/v1/admin/zones/{id}
GET    /api/v1/admin/reports/{type}
```

### 5.3 WebSocket Endpoints

```
# Customer — real-time tracking
wss://api.urbanshopper.co.tz/v1/ws/orders/{orderId}/tracking

# Shopper — offer countdown
wss://api.urbanshopper.co.tz/v1/ws/offers/{offerId}/countdown

# Admin — live dashboard
wss://api.urbanshopper.co.tz/v1/ws/admin/dashboard
```

---

## 6. Data Architecture

### 6.1 Entity Relationship (Core)

```
Customer 1──N Order 1──1 Delivery
Customer 1──N Rating
Customer 1──N Dispute
Customer 1──0..1 CustomerWallet (future)

Shopper 1──N Order
Shopper 1──1 ShopperWallet
Shopper 1──N GPSEvent
Shopper 1──N Rating
Shopper 1──0..1 Device

Order 1──1 Payment
Order 1──N OrderItem
Order N──1 Market
Order N──1 Zone
Order 1──1 Receipt
Order 1──N Notification

Payment 1──N PaymentTransaction

Dispute N──1 Order
Dispute 1──N AuditLogEntry

Rating N──1 Order
Rating N──1 Customer (rater or ratee)
Rating N──1 Shopper (rater or ratee)
```

### 6.2 Key Indexing Strategy

| Table | Index | Rationale |
|-------|-------|-----------|
| `orders` | `(status, zone_id, created_at)` | Fast active order queries per zone for assignment |
| `orders` | `(customer_id, created_at DESC)` | Customer order history |
| `orders` | `(shopper_id, status)` | Shopper's active order lookup |
| `gps_events` | `(shopper_id, timestamp DESC)` | Latest GPS position per shopper |
| `gps_events` | `(timestamp)` | Efficient 30-day purge queries |
| `payment_transactions` | `(payment_id)` | Payment transaction history |
| `disputes` | `(status, created_at)` | Dispute queue management |
| `notifications` | `(recipient_id, recipient_type, status)` | Undelivered notification retry |
| `audit_log` | `(resource_type, resource_id, timestamp)` | Entity audit trail |

### 6.3 Data Retention Implementation

| Data | Active Retention | Cold Storage | Total Retention | Purge Mechanism |
|------|-----------------|--------------|-----------------|-----------------|
| Orders (Completed) | 90 days | Archive DB (1+ yr) | 7 years | Cron job: archive after 90d, purge after 7yr |
| GPS events | 30 days | None | 30 days | Time-partitioned drop |
| Chat messages | 90 days | Archive (1 yr if disputed) | 90 days (or dispute) | Partitioned delete |
| User personal data | Active + 3yr | Archive | Active + 3yr | Cron job: anonymise after 3yr |
| Audit logs | 1 year | Archive (7 yr) | 7 years | Append-only; moved to archive after 1yr |
| Transactional data | 7 years | — | 7 years | No purge (legal requirement) |

---

## 7. Deployment Architecture

### 7.1 MVP Deployment

```
┌─────────────────────────────────────────────────────┐
│                  Cloud Provider                      │
│              (AWS / GCP / Azure)                     │
│                                                      │
│  ┌─────────────────────────────────────────────────┐│
│  │  Region: South Africa (capetown) or Europe      ││
│  │  (for MVP latency to Dar es Salaam)             ││
│  │                                                 ││
│  │  ┌──────────────────────┐ ┌──────────────────┐  ││
│  │  │  Availability Zone A │ │  AZ B            │  ││
│  │  │                      │ │                   │  ││
│  │  │  ┌────────────────┐  │ │  ┌─────────────┐ │  ││
│  │  │  │ API + Services │  │ │  │ API + Svc   │ │  ││
│  │  │  │ (Docker)       │  │ │  │ (Docker)    │ │  ││
│  │  │  ├────────────────┤  │ │  ├─────────────┤ │  ││
│  │  │  │ PostgreSQL     │  │ │  │ PostgreSQL  │ │  ││
│  │  │  │ (Primary)      │  │ │  │ (Standby)   │ │  ││
│  │  │  ├────────────────┤  │ │  ├─────────────┤ │  ││
│  │  │  │ Redis          │  │ │  │ Redis       │ │  ││
│  │  │  └────────────────┘  │ │  │ (Replica)   │ │  ││
│  │  │                      │ │  └─────────────┘ │  ││
│  │  └──────────────────────┘ └──────────────────┘  ││
│  │                                                  ││
│  │  ┌──────────────────────────────────────────┐   ││
│  │  │  Object Storage (S3)                      │   ││
│  │  │  Receipt photos, ID scans, delivery pics  │   ││
│  │  └──────────────────────────────────────────┘   ││
│  │                                                  ││
│  │  ┌──────────────────────────────────────────┐   ││
│  │  │  CDN (CloudFront)                        │   ││
│  │  │  Static assets, app binaries, API cache   │   ││
│  │  └──────────────────────────────────────────┘   ││
│  └──────────────────────────────────────────────────┘│
└──────────────────────────────────────────────────────┘
```

### 7.2 Production (Scaled) Deployment

```
┌──────────────────────────────────────────────────────┐
│  Region: Africa (South Africa)                        │
│                                                       │
│  ┌──────────────────────────┐ ┌────────────────────┐ │
│  │  AZ A                     │ │  AZ B               │ │
│  │  ┌──────────────────────┐│ │  ┌────────────────┐ │ │
│  │  │  EKS / GKE / AKS     ││ │  │  EKS / GKE     │ │ │
│  │  │  Kubernetes Cluster  ││ │  │  K8s Cluster   │ │ │
│  │  │                      ││ │  │                │ │ │
│  │  │  ┌────┐ ┌────┐ ┌──┐ ││ │  │  ┌────┐ ┌──┐  │ │ │
│  │  │  │API │ │Ord │ │Usr│ ││ │  │  │API │ │Pay│  │ │ │
│  │  │  │Gw  │ │Svc │ │Svc│ ││ │  │  │Gw  │ │Svc│  │ │ │
│  │  │  └────┘ └────┘ └──┘ ││ │  │  └────┘ └──┘  │ │ │
│  │  │  ┌────┐ ┌────┐ ┌──┐ ││ │  │  ┌────┐ ┌──┐  │ │ │
│  │  │  │Asgn│ │Dlv │ │Ntf│ ││ │  │  │Dspt│ │Rat│  │ │ │
│  │  │  │Eng │ │Svc │ │Svc│ ││ │  │  │Svc │ │Svc│  │ │ │
│  │  │  └────┘ └────┘ └──┘ ││ │  │  └────┘ └──┘  │ │ │
│  │  │  ┌────┐ ┌────┐     ││ │  │  ┌──────────┐   │ │ │
│  │  │  │GPS │ │Schd│     ││ │  │  │ Fraud    │   │ │ │
│  │  │  │Ing │ │uler│     ││ │  │  │ Detection│   │ │ │
│  │  │  └────┘ └────┘     ││ │  │  └──────────┘   │ │ │
│  │  └──────────────────────┘│ │  └────────────────┘ │ │
│  └──────────────────────────┘ └────────────────────┘ │
│                                                       │
│  ┌──────────────────────────────────────────────────┐ │
│  │  Stateful Services (AZ-independent via HA)       │ │
│  │  ┌────────────┐ ┌──────────┐ ┌────────────────┐ │ │
│  │  │ PostgreSQL  │ │ Timescale│ │  Message Broker│ │ │
│  │  │ (HA: Patroni)│ │ (GPS)   │ │  (Kafka / RMQ) │ │ │
│  │  └────────────┘ └──────────┘ └────────────────┘ │ │
│  │  ┌────────────┐ ┌──────────┐ ┌────────────────┐ │ │
│  │  │ Redis       │ │ Elastic │ │  Object Store  │ │ │
│  │  │ (Cluster)   │ │ Search  │ │  (S3 / GCS)    │ │ │
│  │  └────────────┘ └──────────┘ └────────────────┘ │ │
│  └──────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────┐
│  DR Region (Secondary — warm standby)                │
│  · Replicated PostgreSQL (async)                     │
│  · Read-only service instances                       │
│  · Full activation: RTO 4 hours (NFR-DR-003)         │
└──────────────────────────────────────────────────────┘
```

---

## 8. Security Architecture

### 8.1 Authentication Flow

```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│  Mobile   │     │  API     │     │  User    │     │  Mobile  │
│  App      │     │  Gateway │     │  Service │     │  Money   │
└─────┬────┘     └─────┬────┘     └─────┬────┘     └─────┬────┘
      │                 │                 │                 │
      │ 1. Phone + OTP  │                 │                 │
      │────────────────>│                 │                 │
      │                 │ 2. Validate OTP │                 │
      │                 │────────────────>│                 │
      │                 │                 │ 3. Verify OTP  │
      │                 │                 │ (SMS gateway)  │
      │                 │                 │<──────────────>│
      │                 │                 │                 │
      │                 │ 4. JWT + Refresh│                 │
      │                 │<────────────────│                 │
      │ 5. JWT issued   │                 │                 │
      │<────────────────│                 │                 │
      │                 │                 │                 │
      │ 6. API calls with JWT in header   │                 │
      │═══════════════════════════════════>│                 │
      │ (Gateway validates JWT on every   │                 │
      │  request before routing)          │                 │
```

### 8.2 Security Layers

| Layer | Controls | NFR Reference |
|-------|----------|---------------|
| **Network** | VPC, private subnets, security groups, WAF, DDoS protection | NFR-SEC-001 |
| **Transport** | TLS 1.2+ for all external and internal communication | NFR-SEC-001 |
| **Edge** | API Gateway: rate limiting (100/30/10 req/min), IP whitelist for admin, WAF for SQL injection/XSS | NFR-SEC-006 |
| **Application** | JWT with device binding, RBAC (3 admin tiers), input validation, parameterised queries | NFR-SEC-005 through -009 |
| **Data** | AES-256 at rest, bcrypt for passwords, HSM/KMS for keys, PII masking in logs | NFR-SEC-002 through -004 |
| **Audit** | Immutable audit logs, security event logging, anomaly detection alerts | NFR-SEC-011 through -014 |

### 8.3 Data Classification

| Classification | Examples | Storage Requirements | Transmission Requirements |
|----------------|----------|---------------------|--------------------------|
| **Highly Restricted** | Passwords, OTPs, encryption keys | Bcrypt hashed / HSM, never logged | Never transmitted in plaintext |
| **Restricted** | PII (name, phone, ID numbers, payment accounts) | AES-256 encrypted at rest | TLS 1.2+, masked in logs |
| **Internal** | Order data, GPS locations, ratings, chat logs | Standard encryption at rest | TLS 1.2+ |
| **Public** | App version info, static content, public FAQs | No encryption required | Standard HTTPS |

---

## 9. Integration Architecture

### 9.1 Payment Provider Abstraction

```
┌─────────────────────────────────────────────────────────┐
│                    Payment Service                        │
│                                                          │
│  ┌──────────────────────────────────────────────────┐   │
│  │           Payment Provider Interface             │   │
│  │  ┌────────────────────────────────────────────┐  │   │
│  │  │ +preAuth(customer, amount): PreAuthResult   │  │   │
│  │  │ +capture(preAuthRef, amount): CaptureResult │  │   │
│  │  │ +refund(transactionRef, amount): RefundResult│  │   │
│  │  │ +payout(shopper, amount): PayoutResult       │  │   │
│  │  │ +checkStatus(reference): StatusResult        │  │   │
│  │  └────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────┘   │
│                                                          │
│           ┌──────────┐  ┌──────────┐  ┌──────────┐      │
│           │  M-Pesa  │  │  Mixx    │  │  Airtel  │      │
│           │ Adapter  │  │  Adapter │  │  Adapter │      │
│           └──────────┘  └──────────┘  └──────────┘      │
│                                                          │
│  Primary: M-Pesa (default)                               │
│  Fallback: Mixx (if M-Pesa unavailable)                  │
│  V1: Airtel Money (configured via settings)              │
└──────────────────────────────────────────────────────────┘
```

### 9.2 External Integration SLAs & Fallbacks

| Integration | SLA Expectation | Fallback Behaviour | Monitoring |
|-------------|----------------|--------------------|------------|
| M-Pesa API | 99.5% uptime, < 5s response | Retry 2x within 5 min. Then try Mixx. If all fail, offer COD. | Health check every 30s. Alert on failure. |
| Mixx API | 99% uptime, < 5s response | Retry 2x. Then try M-Pesa. | Health check every 60s. |
| SMS Gateway | 99% uptime, < 3s send time | Queue and retry for 15 min. If persistent failure, alert ops. | Delivery status tracking. Alert on > 10% failure rate. |
| Maps API | 99.9% uptime, < 1s response | Fall back to Haversine × 1.5 ratio (road approximate). | Health check every 30s. Alert on failure. |
| Push Notifications (FCM) | 99.9% delivery within 5s | SMS fallback for critical notifications (offers, payment failures). | Delivery receipt tracking. |

---

## 10. Scalability & Performance Design

### 10.1 Bottleneck Analysis

| Component | MVP Limit | Scaling Strategy |
|-----------|-----------|------------------|
| **PostgreSQL** | Single writer instance | Read replicas for reporting. Zone-based sharding. Connection pooling (PgBouncer). |
| **GPS Ingestion** | ~1,000 pings/sec | Kafka partitioning by shopper ID. TimescaleDB hypertables. Auto-scaling ingestion service. |
| **Assignment Engine** | ~100 offers/sec | In-memory scoring with Redis. Stateless — scale horizontally. Batch GPS proximity calculations. |
| **File Storage (Photos)** | MVP: local disk → V1: S3/CDN | Direct-to-S3 upload presigned URLs. CDN caching. Async image optimisation. |
| **Push Notifications** | ~500/sec | Batch sends via FCM. Queue-based delivery with retry. Priority queuing. |

### 10.2 Caching Strategy

| Cache | What | TTL | Invalidation |
|-------|------|-----|--------------|
| **Redis: Active shoppers** | Shopper IDs + GPS + score per zone | 10 seconds (live) | Real-time update on every GPS ping |
| **Redis: Session** | JWT session state | 30 minutes | On logout or token expiry |
| **Redis: Rate limits** | Request counters | Window duration | Time-based expiry |
| **Redis: Offer state** | Current offer + timer per shopper | 35 seconds (offer + buffer) | Cleared on accept/decline/timeout |
| **CDN: Static assets** | App images, icons, HTML | 24 hours | Cache invalidation on deployment |
| **Application: Zone config** | Zone policies, pricing | 5 minutes | Updated on zone config change (message broker) |
| **Application: Price estimates** | Item suggestion cache | 1 hour | Background refresh |

### 10.3 Performance Budgets

```
┌─────────────────────────────────────────────────────┐
│                    PERFORMANCE BUDGET                │
│                                                      │
│  Order Creation Flow (customer perspective):          │
│  ┌───────────────────────────────────────────────┐  │
│  │  Tap "Submit" → API gateway (20ms)            │  │
│  │                → Validate order (50ms)         │  │
│  │                → Pre-auth (M-Pesa API: 5s)    │  │
│  │                → Create order in DB (30ms)     │  │
│  │                → Queue for assignment (10ms)   │  │
│  │                → Response to client (10ms)     │  │
│  │                ─────────────────────────       │  │
│  │                Total: ~5.2s (dominated by      │  │
│  │                M-Pesa API call)                │  │
│  └───────────────────────────────────────────────┘  │
│                                                      │
│  Assignment Flow:                                    │
│  ┌───────────────────────────────────────────────┐  │
│  │  GPS ping received (10ms)                     │  │
│  │  → Update Redis position (5ms)                │  │
│  │  → Check eligible shoppers (20ms)             │  │
│  │  → Calculate scores (50ms for 20 candidates)  │  │
│  │  → Send offer (push: 500ms avg)               │  │
│  │  ─────────────────────────────                 │  │
│  │  Total: ~85ms server-side + push delivery     │  │
│  └───────────────────────────────────────────────┘  │
│                                                      │
│  GPS Ingestion (per ping):                           │
│  ┌───────────────────────────────────────────────┐  │
│  │  Receive (5ms) → Validate (5ms) → Store (10ms)│  │
│  │  → Spoofing check (10ms) → Publish (5ms)      │  │
│  │  ─────────────────────────────                 │  │
│  │  Total: ~35ms per ping                        │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

---

*This document is Phase 7 of the Urban Shopper Platform specification. It feeds into Phase 8 (IEEE 29148 SRS) and Phase 10 (API Specification).*
