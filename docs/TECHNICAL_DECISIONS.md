# Urban Shopper Platform - Technical Decisions Record

> **Document Type:** Architecture Decision Record (ADR)  
> **Status:** Active  
> **Date:** 2026-07-24  
> **Version:** 1.0  
> **Purpose:** Record all significant technical decisions with rationale, alternatives considered, and consequences

---

## 1. Introduction

This document records technical decisions made during the development of the Urban Shopper Platform. Each decision follows the MADR (Markdown Architectural Decision Records) format.

## 2. Decision Records

### ADR-001: Microservices Architecture Pattern

**Status:** Accepted  
**Date:** 2026-07-24  
**Context:** The Urban Shopper Platform requires independent scaling of different business capabilities (assignment, payment, delivery), team autonomy, and technology flexibility.

**Decision:** Adopt a microservices-dominant architecture with 9 core services:
1. User Service
2. Order Service  
3. Assignment Engine
4. Payment Service
5. Delivery Service
6. Dispute Service
7. Rating Service
8. Notification Service
9. GPS Ingestion Service

**Rationale:**
- Independent scaling of compute-intensive services (Assignment Engine, GPS Ingestion)
- Team autonomy for different business domains
- Technology flexibility per service needs
- Fault isolation between services
- Aligns with business capabilities defined in domain model

**Alternatives Considered:**
1. **Monolithic Architecture:** Simpler deployment but limits scaling and team autonomy
2. **Service-Oriented Architecture (SOA):** More heavyweight with ESB, less suitable for cloud-native
3. **Serverless Functions:** Good for event-driven parts but complex for stateful services

**Consequences:**
- ✅ Independent deployment and scaling
- ✅ Technology flexibility per service
- ✅ Team autonomy and ownership
- ⚠️ Increased operational complexity
- ⚠️ Distributed tracing required
- ⚠️ Service-to-service communication overhead

---

### ADR-002: Spring Boot + Java Backend Framework

**Status:** Accepted  
**Date:** 2026-07-24  
**Context:** Need a mature, performant backend framework with strong ecosystem support for building 9 microservices.

**Decision:** Use Spring Boot 3.x with Java 21 as the primary backend framework.

**Rationale:**
- Mature ecosystem with extensive libraries
- Strong typing and compile-time safety
- Excellent performance (GraalVM native image support)
- Spring Security for robust authentication/authorization
- Spring Data JPA for database operations
- Large community and talent pool
- Proven in production at scale

**Alternatives Considered:**
1. **Node.js/Express:** Faster development but weaker typing, callback hell
2. **Python/Django/Flask:** Rapid prototyping but performance concerns at scale
3. **Go:** Excellent performance but smaller ecosystem, learning curve
4. **.NET Core:** Good performance but less common in Tanzanian market

**Consequences:**
- ✅ Strong typing and compile-time safety
- ✅ Mature ecosystem and libraries
- ✅ Excellent performance characteristics
- ⚠️ Higher memory footprint than Go/Node.js
- ⚠️ Slower startup time (mitigated with GraalVM)
- ⚠️ Java expertise required

---

### ADR-003: Flutter for Mobile Applications

**Status:** Accepted  
**Date:** 2026-07-24  
**Context:** Need cross-platform mobile apps for both Android and iOS with single codebase, good performance, and access to device features (GPS, camera).

**Decision:** Use Flutter 3.x for both Customer App and Shopper App.

**Rationale:**
- Single codebase for Android and iOS
- Native performance with compiled code
- Excellent hot reload for developer productivity
- Strong widget library and material design
- Good GPS and camera plugin support
- Growing community and ecosystem
- Backed by Google with long-term support

**Alternatives Considered:**
1. **React Native:** JavaScript bridge performance issues, native module complexity
2. **Native Android + iOS:** Maximum performance but 2x development cost
3. **Kotlin Multiplatform:** Emerging but less mature, smaller ecosystem

**Consequences:**
- ✅ Single codebase for both platforms
- ✅ Native performance
- ✅ Fast development with hot reload
- ⚠️ Larger app size than native
- ⚠️ Dart language learning curve
- ⚠️ Some platform-specific features require plugins

---

### ADR-004: PostgreSQL with PostGIS

**Status:** Accepted  
**Date:** 2026-07-24  
**Context:** Need relational database with geospatial capabilities for location-based queries (distance calculation, zone boundary containment) and reliable storage for transactional data.

**Decision:** Use PostgreSQL 16+ with PostGIS extension for geospatial capabilities. TimescaleDB was considered but deferred — standard PostgreSQL with proper indexing and partitioning is sufficient for 30-day GPS data retention at MVP scale.

**Rationale:**
- PostgreSQL: ACID compliance, JSONB support, strong reliability
- PostGIS: Industry-standard geospatial extension, ST_Distance, ST_Contains
- Standard PostgreSQL avoids additional license complexity and operational overhead
- Hibernate Spatial provides seamless PostGIS integration with Spring Boot
- Proper indexing (GIST) and monthly table partitioning handle GPS data volume at MVP scale
- If GPS volume grows beyond PostgreSQL's capability, TimescaleDB can be added as a hypertable later without affecting transactional data

**Alternatives Considered:**
1. **MySQL + Custom geospatial:** Less mature geospatial support
2. **TimescaleDB:** Valuable for time-series but adds license complexity. Defer until GPS volume justifies it
3. **MongoDB:** Document store with weaker transactional guarantees

**Consequences:**
- ✅ Strong geospatial capabilities via PostGIS
- ✅ Single database technology stack
- ✅ No additional license costs
- ✅ Hibernate Spatial integration with Spring Boot
- ⚠️ Geospatial query performance tuning required (GIST indexes)
- ⚠️ GPS data growth requires partitioning strategy (monthly partitions)

---

### ADR-005: Event-Driven Communication with RabbitMQ

**Status:** Accepted  
**Date:** 2026-07-24  
**Context:** Need asynchronous communication between microservices for real-time events (GPS pings, order state changes, notifications).

**Decision:** Use RabbitMQ as the primary message broker for event-driven communication.

**Rationale:**
- Proven reliability and at-least-once delivery guarantees
- Good performance for our scale (10k concurrent shoppers)
- Flexible routing with exchanges and queues
- Dead letter queue support for error handling
- Good Spring Boot integration
- Tanzanian operational familiarity

**Alternatives Considered:**
1. **Apache Kafka:** Better for high-throughput log streaming but more complex
2. **AWS SQS/SNS:** Cloud-native but vendor lock-in, higher cost
3. **Redis Pub/Sub:** Simpler but no persistence, weaker delivery guarantees

**Consequences:**
- ✅ Reliable message delivery
- ✅ Good Spring Boot integration
- ✅ Flexible routing patterns
- ⚠️ Operational overhead for clustering
- ⚠️ Message schema evolution management required

---

### ADR-006: Next.js for Admin Dashboard

**Status:** Accepted  
**Date:** 2026-07-24  
**Context:** Need performant admin dashboard with server-side rendering, good TypeScript support, and React ecosystem.

**Decision:** Use Next.js 14 with TypeScript for the Admin Dashboard.

**Rationale:**
- Server-side rendering for dashboard performance
- Excellent TypeScript support
- React ecosystem with large component library
- Built-in API routes for backend-for-frontend pattern
- Good deployment options (Vercel, Docker)
- Growing popularity and community

**Alternatives Considered:**
1. **React + Vite:** Faster dev server but no SSR out of box
2. **Angular:** Full framework but steeper learning curve
3. **Vue/Nuxt:** Good alternative but smaller React ecosystem

**Consequences:**
- ✅ Server-side rendering performance
- ✅ TypeScript safety
- ✅ React ecosystem benefits
- ⚠️ Next.js specific patterns to learn
- ⚠️ Deployment considerations for SSR

---

### ADR-007: Cloud Provider Selection

**Status:** Pending Decision  
**Date:** 2026-07-24  
**Context:** Need to select cloud provider for infrastructure deployment considering Tanzanian market, costs, and services needed.

**Decision:** [To be decided during Phase 0 Week 1]

**Options:**
1. **AWS:** Most comprehensive services, global presence, higher cost
2. **Google Cloud:** Good data analytics, competitive pricing
3. **Azure:** Strong enterprise integration, Microsoft ecosystem
4. **Local Tanzanian provider:** Lower latency, data sovereignty

**Evaluation Criteria:**
- Cost for Tanzanian operations
- Latency for Dar es Salaam users
- Data sovereignty and compliance
- Service availability (PostgreSQL, Redis, object storage)
- Tanzanian developer familiarity

---

### ADR-008: Container Orchestration

**Status:** Pending Decision  
**Date:** 2026-07-24  
**Context:** Need container orchestration for 9 microservices with scaling, service discovery, and deployment automation.

**Decision:** [To be decided during Phase 0]

**Options:**
1. **Kubernetes:** Industry standard, maximum flexibility, operational complexity
2. **AWS ECS/EKS:** Managed Kubernetes, AWS integration
3. **Docker Swarm:** Simpler but less features, declining adoption
4. **Nomad:** Simpler alternative, less ecosystem

**Considerations:**
- Team Kubernetes expertise
- Operational overhead vs managed service
- Tanzanian market support availability

---

## 3. Decision Process

### 3.1 Decision Making Authority
- **CTO/Engineering Lead:** Final approval for architectural decisions
- **Technical Leads:** Proposal and evaluation of alternatives
- **Development Team:** Input and feedback on decisions

### 3.2 Decision Criteria
1. **Alignment with Business Requirements:** Must support business capabilities
2. **Technical Feasibility:** Team expertise and implementation complexity
3. **Operational Viability:** Maintenance, monitoring, and scaling considerations
4. **Cost Effectiveness:** Development and operational costs
5. **Future Flexibility:** Ability to adapt to changing requirements

### 3.3 Review Process
1. **Proposal:** Technical lead proposes decision with alternatives
2. **Discussion:** Team reviews during architecture review meeting
3. **Decision:** CTO/Engineering Lead makes final decision
4. **Documentation:** Decision recorded in this document
5. **Implementation:** Team implements according to decision
6. **Review:** Periodic review of decisions for relevance

---

## 4. Pending Decisions

| ID | Topic | Priority | Due Date | Owner |
|----|-------|----------|----------|-------|
| ADR-007 | Cloud Provider Selection | High | Week 1 | CTO |
| ADR-008 | Container Orchestration | High | Week 2 | DevOps Lead |
| ADR-009 | Monitoring Stack Selection | Medium | Week 2 | DevOps |
| ADR-010 | CI/CD Tool Selection | Medium | Week 1 | DevOps |
| ADR-011 | Testing Strategy | Medium | Week 3 | QA Lead |

---

## 5. Change Log

| Date | Version | Changes | Author |
|------|---------|---------|--------|
| 2026-07-24 | 1.0 | Initial document with first 6 ADRs | CTO |

---

**Next:** Begin documenting specific implementation decisions as Phase 0 progresses.