# Urban Shopper Platform

> **A digital marketplace connecting customers with verified independent shoppers in Tanzania.**  
> *Status: Ready for Phase 0 Development*

---

## Project Structure

```
urban-shopper/
├── docs/                      # Complete specification (22 files)
│   ├── 01-business-vision-and-strategy.md
│   ├── 02-domain-model.md
│   ├── 03-business-rules.md        ← 134 rules — the platform's constitution
│   ├── 04-state-machines.md
│   ├── 05-business-process-models.md
│   ├── 06-non-functional-requirements.md
│   ├── 07-system-architecture.md
│   ├── 08-ieee-29148-srs.md        ← 96 functional requirements
│   ├── 09-ux-specification.md
│   ├── 10-api-specification.md
│   ├── 11-database-design.md
│   ├── 12-implementation-roadmap.md
│   ├── SUMMARY.md
│   ├── README.md                   ← Spec index
│   ├── TECHNICAL_DECISIONS.md      ← Architecture Decision Records
│   ├── PROJECT_STRUCTURE.md        ← Project organisation
│   └── API_CONTRACTS.md            ← API contract patterns
├── .gitignore
└── README.md                   ← This file
```

## Quick Start

1. Read `docs/SUMMARY.md` for the full project overview
2. Read `docs/12-implementation-roadmap.md` for the development plan
3. Begin Phase 0: Foundation (Infrastructure, CI/CD, Auth, Database)

## Technology Stack

| Layer | Technology |
|-------|------------|
| **Mobile Apps** | Flutter (Customer + Shopper) |
| **Admin Dashboard** | Next.js + TypeScript |
| **Backend** | Spring Boot 3.x + Java 21 |
| **Database** | PostgreSQL + PostGIS |
| **Cache** | Redis |
| **Messaging** | RabbitMQ |
| **Deployment** | Docker Compose + Nginx |
| **Monitoring** | Prometheus + Grafana |
