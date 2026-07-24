# Urban Shopper Platform - Project Structure

> **Document Type:** Project Management  
> **Status:** Active  
> **Date:** 2026-07-24  
> **Version:** 1.0  
> **Source of Truth:** This document defines the project structure and organization

---

## 1. Project Overview

Urban Shopper is a digital marketplace platform connecting customers with verified independent shoppers in Tanzania. This document defines the project structure for development.

## 2. Directory Structure

```
urban-shopper/
├── docs/                          # Documentation
│   ├── specifications/            # 12 core specification documents
│   ├── architecture/              # Technical architecture documents
│   └── api/                       # API documentation
├── backend/                       # Backend services (Spring Boot)
│   ├── user-service/              # User management service
│   ├── order-service/             # Order lifecycle service
│   ├── assignment-engine/         # GPS-based shopper matching
│   ├── payment-service/           # Payment processing
│   ├── delivery-service/          # Delivery tracking
│   ├── dispute-service/           # Dispute resolution
│   ├── rating-service/            # Rating system
│   ├── notification-service/      # Push/SMS notifications
│   └── gps-ingestion-service/     # GPS data processing
├── mobile/                        # Mobile applications (Flutter)
│   ├── customer-app/              # Customer-facing app
│   └── shopper-app/               # Shopper-facing app
├── web/                           # Web applications
│   └── admin-dashboard/           # Admin dashboard (Next.js)
├── infrastructure/                # Infrastructure as Code
│   ├── terraform/                 # Terraform configurations
│   ├── kubernetes/                # K8s manifests
│   └── docker/                    # Docker configurations
├── shared/                        # Shared resources
│   ├── libs/                      # Shared libraries
│   ├── contracts/                 # API contracts/interfaces
│   └── ui-kit/                    # Shared UI components
└── tools/                         # Development tools
    ├── scripts/                   # Utility scripts
    └── ci-cd/                     # CI/CD configurations
```

## 3. Source of Truth Documents

### 3.1 Core Specifications (Already Exist)
1. `01-business-vision-and-strategy.md` - Business vision, mission, value proposition
2. `02-domain-model.md` - 22 business entities with relationships
3. `03-business-rules.md` - 134 business rules across 13 categories
4. `04-state-machines.md` - 4 formal state machines (Order, Shopper, Payment, Dispute)
5. `05-business-process-models.md` - 6 BPMN process models
6. `06-non-functional-requirements.md` - 118 NFRs across 13 categories
7. `07-system-architecture.md` - 9 microservices, tech stack, deployment
8. `08-ieee-29148-srs.md` - IEEE compliant SRS with 96 functional requirements
9. `09-ux-specification.md` - UX design for Customer/Shopper/Admin apps
10. `10-api-specification.md` - ~80 REST endpoints + 3 WebSocket channels
11. `11-database-design.md` - 20 tables across 8 schemas
12. `12-implementation-roadmap.md` - 7-phase implementation plan (20 weeks)

### 3.2 New Source-of-Truth Documents Needed
1. **`TECHNICAL_DECISIONS.md`** - Records all technical decisions with rationale
2. **`DEVELOPMENT_SETUP.md`** - Development environment setup guide
3. **`API_CONTRACTS.md`** - API contracts and interface definitions
4. **`DEPLOYMENT_GUIDE.md`** - Deployment procedures and checklists
5. **`TESTING_STRATEGY.md`** - Testing approach and coverage requirements
6. **`SECURITY_POLICY.md`** - Security policies and compliance requirements

## 4. Development Workflow

### 4.1 Branch Strategy
- `main` - Production-ready code
- `develop` - Integration branch for features
- `feature/*` - Feature branches (e.g., `feature/user-auth`)
- `release/*` - Release preparation branches
- `hotfix/*` - Critical bug fixes

### 4.2 Commit Convention
```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

### 4.3 Code Review Process
1. Create feature branch from `develop`
2. Implement changes with tests
3. Create Pull Request to `develop`
4. Minimum 1 reviewer approval required
5. All CI checks must pass
6. Merge using squash commit

## 5. Technology Stack

### Backend
- **Framework:** Spring Boot 3.x + Java 21
- **Database:** PostgreSQL 16+ with PostGIS extension
- **Cache:** Redis
- **Messaging:** RabbitMQ
- **Object Storage:** S3-compatible (MinIO/Cloudflare R2/Amazon S3)
- **API Gateway:** Kong or Spring Cloud Gateway

### Mobile (Flutter)
- **Framework:** Flutter 3.x
- **State Management:** Riverpod/Bloc
- **Local Storage:** Hive/SQLite
- **Maps:** Google Maps Platform
- **Push Notifications:** Firebase Cloud Messaging

### Web (Admin Dashboard)
- **Framework:** Next.js 14 + TypeScript
- **UI Library:** React + Tailwind CSS
- **State Management:** Zustand/Redux Toolkit

## 6. Phase 0 Foundation Tasks

Based on `12-implementation-roadmap.md`, Phase 0 includes:

### Week 1-2: Infrastructure
- Cloud account setup (AWS/GCP/Azure)
- VPC, subnets, security groups
- PostgreSQL + PostGIS deployment
- Redis cluster deployment
- CI/CD pipeline (GitHub Actions)
- Monitoring stack (Grafana/Prometheus)
- Object storage (S3/GCS)
- CDN configuration

### Week 3-4: Core Services Skeleton
- API Gateway setup
- User service: auth + OTP + JWT
- User service: customer/shopper CRUD
- Database schema deployment
- Mobile app project scaffolding
- Admin dashboard scaffolding
- Shared component library

## 7. Quality Gates

### Code Quality
- Test coverage ≥ 80%
- No critical security vulnerabilities
- Static analysis passing (SonarQube)
- Code style compliance

### Performance
- API response times meet NFR targets
- Load testing passes 10x MVP load
- 99.5% uptime target

### Security
- OWASP Top 10 compliance
- Regular security audits
- Penetration testing passed

## 8. Team Roles & Responsibilities

### Development Team
- **Backend Engineers:** Microservices development
- **Mobile Engineers:** Flutter app development
- **Frontend Engineers:** Admin dashboard development
- **DevOps Engineers:** Infrastructure and deployment

### Quality Assurance
- **QA Engineers:** Test automation and manual testing
- **Security Engineers:** Security testing and compliance

### Project Management
- **Product Owner:** Requirements and prioritization
- **Scrum Master:** Agile process facilitation
- **CTO/Engineering Lead:** Technical direction and architecture

---

**Next Steps:** 
1. Create missing source-of-truth documents
2. Set up development environment
3. Begin Phase 0 infrastructure setup
4. Establish CI/CD pipeline