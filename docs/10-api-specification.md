# Urban Shopper Platform — API Specification

> **Document Type:** Technical Specification  
> **Status:** Complete  
> **Date:** 2026-07-24  
> **Version:** 1.0  
> **Cross-Reference:** Domain Model — `02-domain-model.md`, SRS — `08-ieee-29148-srs.md`, System Architecture — `07-system-architecture.md`  
> **Phase:** 10 of 12

---

## Table of Contents

1. [API Design Conventions](#1-api-design-conventions)
2. [Authentication](#2-authentication)
3. [Customer API](#3-customer-api)
4. [Shopper API](#4-shopper-api)
5. [Order API](#5-order-api)
6. [Payment API](#6-payment-api)
7. [Delivery API](#7-delivery-api)
8. [Dispute API](#8-dispute-api)
9. [Rating API](#9-rating-api)
10. [Notification API](#10-notification-api)
11. [Admin API](#11-admin-api)
12. [Internal Service API](#12-internal-service-api)
13. [WebSocket API](#13-websocket-api)

---

## 1. API Design Conventions

### Base URL

```
Production:     https://api.urbanshopper.co.tz/api/v1
Sandbox:        https://sandbox-api.urbanshopper.co.tz/api/v1
WebSocket:      wss://api.urbanshopper.co.tz/api/v1/ws
```

### Request/Response Format

All requests and responses use `Content-Type: application/json`.

### Standard Response Envelope

```json
{
  "success": true,
  "data": { ... },
  "meta": {
    "page": 1,
    "per_page": 20,
    "total": 100,
    "timestamp": "2026-07-24T10:30:00+03:00"
  },
  "error": null
}
```

### Error Response

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INSUFFICIENT_BALANCE",
    "message": "Insufficient mobile money balance to place order.",
    "details": {
      "required": 36500,
      "available": 10000
    }
  }
}
```

### HTTP Status Codes

| Code | Usage |
|------|-------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request — validation error |
| 401 | Unauthenticated — missing/invalid JWT |
| 403 | Forbidden — authenticated but not authorised |
| 404 | Not Found |
| 409 | Conflict — duplicate or state conflict |
| 422 | Unprocessable Entity — business rule violation |
| 429 | Rate Limited |
| 500 | Internal Server Error |

### Pagination

```http
GET /api/v1/orders?page=2&per_page=20&sort=created_at:desc&status=completed
```

Response includes `meta` object with `page`, `per_page`, `total`.

### Authentication Header

```http
Authorization: Bearer <jwt_token>
```

### ID Format

All resource IDs are UUID v4 format: `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`

Error codes use snake_case: `insufficient_balance`, `order_not_found`, `invalid_state_transition`

Timestamps use ISO 8601 with EAT offset: `2026-07-24T10:30:00+03:00`

---

## 2. Authentication

### 2.1 Request OTP

```http
POST /api/v1/auth/otp
Content-Type: application/json

{
  "phone": "255712345678",
  "role": "customer" | "shopper"
}

Response 200:
{
  "success": true,
  "data": {
    "otp_sent": true,
    "retry_after_seconds": 60
  }
}
```

### 2.2 Verify OTP & Get Token

```http
POST /api/v1/auth/verify
Content-Type: application/json

{
  "phone": "255712345678",
  "otp": "123456",
  "device_id": "fingerprint-hash",
  "device_platform": "android",
  "push_token": "fcm-token-here"
}

Response 200:
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "refresh_token": "eyJhbGciOiJIUzI1NiIs...",
    "expires_in": 1800,
    "user": {
      "id": "uuid",
      "phone": "255712345678",
      "name": "Aisha Mohamed",
      "role": "customer",
      "status": "active"
    }
  }
}
```

### 2.3 Refresh Token

```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refresh_token": "eyJhbGciOiJIUzI1NiIs..."
}

Response 200:
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "expires_in": 1800
  }
}
```

---

## 3. Customer API

### 3.1 Register Customer

```http
POST /api/v1/customers/register
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "Aisha Mohamed",
  "language": "sw",        // "sw" | "en"
  "notification_preferences": {
    "push": true,
    "sms": true,
    "in_app": true
  },
  "accepted_terms": true
}

Response 201:
{
  "success": true,
  "data": {
    "id": "uuid",
    "name": "Aisha Mohamed",
    "phone": "255712345678",
    "language": "sw",
    "status": "active",
    "trust_score": 50,
    "created_at": "2026-07-24T10:30:00+03:00"
  }
}
```

### 3.2 Get Customer Profile

```http
GET /api/v1/customers/{id}/profile
Authorization: Bearer <token>

Response 200:
{
  "success": true,
  "data": {
    "id": "uuid",
    "name": "Aisha Mohamed",
    "phone": "255712345678",
    "language": "sw",
    "notification_preferences": { ... },
    "trust_score": 85,
    "total_orders": 24,
    "status": "active",
    "created_at": "2026-03-15T08:00:00+03:00"
  }
}
```

### 3.3 Update Customer Profile

```http
PATCH /api/v1/customers/{id}/profile
Authorization: Bearer <token>

{
  "name": "Aisha M. Mohamed",
  "language": "en",
  "notification_preferences": {
    "sms": false
  }
}
```

### 3.4 Get Customer Order History

```http
GET /api/v1/customers/{id}/orders?page=1&per_page=20&status=completed
Authorization: Bearer <token>

Response 200:
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "order_number": "URB-20260724-0001",
      "status": "completed",
      "item_count": 8,
      "total_cost": 36500,
      "shopper_name": "Juma Ali",
      "created_at": "2026-07-24T09:00:00+03:00"
    }
  ],
  "meta": { "page": 1, "per_page": 20, "total": 24 }
}
```

### 3.5 Deactivate Customer Account

```http
POST /api/v1/customers/{id}/deactivate
Authorization: Bearer <token>

{
  "reason": "not_using"    // optional
}

Response 200:
{
  "success": true,
  "data": {
    "status": "deactivated",
    "deactivated_at": "2026-07-24T10:30:00+03:00"
  }
}
```

---

## 4. Shopper API

### 4.1 Register Shopper

```http
POST /api/v1/shoppers/register
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "Juma Ali",
  "emergency_contact": {
    "name": "Mama Juma",
    "phone": "255712345679",
    "relationship": "mother"
  },
  "transport": {
    "type": "motorcycle",
    "documents": {
      "registration": "pending_upload",
      "driving_licence": "pending_upload",
      "insurance": "pending_upload"
    }
  },
  "accepted_terms": true,
  "accepted_code_of_conduct": true
}

Response 201:
{
  "success": true,
  "data": {
    "id": "uuid",
    "name": "Juma Ali",
    "phone": "255712345678",
    "status": "pending_verification",
    "transport_type": "motorcycle",
    "created_at": "2026-07-24T10:30:00+03:00"
  }
}
```

### 4.2 Upload Identity Documents

```http
POST /api/v1/shoppers/{id}/identity
Content-Type: multipart/form-data
Authorization: Bearer <token>

{
  "id_photo_front": <file>,
  "id_photo_back": <file>,
  "selfie": <file>,
  "id_type": "national_id"     // "national_id" | "passport" | "voter_id"
}

Response 200:
{
  "success": true,
  "data": {
    "identity_status": "pending_review",
    "submitted_at": "2026-07-24T10:30:00+03:00"
  }
}
```

### 4.3 Get Shopper Profile

```http
GET /api/v1/shoppers/{id}/profile
Authorization: Bearer <token>

Response 200:
{
  "success": true,
  "data": {
    "id": "uuid",
    "name": "Juma Ali",
    "phone": "255712345678",
    "status": "active",
    "transport_type": "motorcycle",
    "rating": 4.5,
    "rating_count": 87,
    "tier": "gold",
    "orders_completed": 234,
    "acceptance_rate": 82.5,
    "completion_rate": 96.0,
    "is_online": true
  }
}
```

### 4.4 Update Shopper Availability

```http
POST /api/v1/shoppers/{id}/availability
Authorization: Bearer <token>

{
  "is_online": true    // true = Go Online, false = Go Offline
}

Response 200:
{
  "success": true,
  "data": {
    "is_online": true,
    "since": "2026-07-24T10:30:00+03:00"
  }
}
```

### 4.5 Get Shopper Wallet

```http
GET /api/v1/shoppers/{id}/wallet
Authorization: Bearer <token>

Response 200:
{
  "success": true,
  "data": {
    "available_balance": 45000,
    "pending_balance": 12000,
    "lifetime_earnings": 1250000,
    "withdrawal_min": 2000,
    "withdrawal_max_daily": 200000,
    "today_withdrawn": 0
  }
}
```

### 4.6 Withdraw from Wallet

```http
POST /api/v1/shoppers/{id}/wallet/withdraw
Authorization: Bearer <token>

{
  "amount": 30000
}

Response 200:
{
  "success": true,
  "data": {
    "transaction_id": "uuid",
    "amount": 30000,
    "new_balance": 15000,
    "status": "processing"
  }
}
```

---

## 5. Order API

### 5.1 Create Order

```http
POST /api/v1/orders
Content-Type: application/json
Authorization: Bearer <token>

{
  "delivery_location": {
    "latitude": -6.7924,
    "longitude": 39.2083,
    "address_text": "123 Mikocheni Street, Dar es Salaam",
    "landmark": "Near Mlimani City"
  },
  "items": [
    {
      "name": "Rice",
      "quantity": 5,
      "unit": "kg",
      "preferred_brand": "Taifa",
      "max_price": 18000,
      "notes": "not too hard"
    }
  ],
  "shopping_preference": "balanced",
  "delivery_time": "asap",               // "asap" | "scheduled"
  "scheduled_window": null,              // ISO 8601 if scheduled
  "payment_method": "mpesa",            // "mpesa" | "mixx" | "cod"
  "substitution_default": "contact_me",  // "best_match" | "contact_me" | "no_substitutions"
  "market_id": "uuid",
  "zone_id": "uuid"
}

Response 201:
{
  "success": true,
  "data": {
    "id": "uuid",
    "order_number": "URB-20260724-0001",
    "status": "awaiting_payment_verification",
    "estimated_costs": {
      "item_estimate": 30000,
      "service_fee": 3000,
      "delivery_fee": 3500,
      "total": 36500
    },
    "created_at": "2026-07-24T10:30:00+03:00"
  }
}
```

### 5.2 Get Order Status

```http
GET /api/v1/orders/{id}
Authorization: Bearer <token>

Response 200:
{
  "success": true,
  "data": {
    "id": "uuid",
    "order_number": "URB-20260724-0001",
    "status": "accepted",
    "items_count": 8,
    "items_found": 0,
    "items_substituted": 0,
    "items_unavailable": 0,
    "shopper": {
      "id": "uuid",
      "name": "Juma Ali",
      "rating": 4.5,
      "photo_url": "https://..."
    },
    "eta_to_market": "8 min",
    "timeline": [
      { "event": "created", "timestamp": "2026-07-24T10:30:00+03:00" },
      { "event": "accepted", "timestamp": "2026-07-24T10:30:45+03:00" }
    ],
    "final_costs": null       // populated after receipt
  }
}
```

### 5.3 Cancel Order

```http
POST /api/v1/orders/{id}/cancel
Content-Type: application/json
Authorization: Bearer <token>

{
  "reason": "changed_mind",
  "cancelled_by": "customer"   // "customer" | "shopper" | "platform"
}

Response 200:
{
  "success": true,
  "data": {
    "status": "cancelled",
    "cancellation_fee": 1750,
    "refund_amount": 34750,
    "shopper_compensation": 1750
  }
}
```

### 5.4 Update Item Status (Shopper)

```http
POST /api/v1/orders/{orderId}/items/{itemId}/status
Content-Type: application/json
Authorization: Bearer <token>

{
  "status": "substituted",        // "found" | "substituted" | "not_available"
  "substitution_note": "Replaced with brand X — same quality",
  "substitution_photo": <optional_base64_image>,
  "actual_price": 4500
}

Response 200:
{
  "success": true,
  "data": {
    "item_id": "uuid",
    "status": "substituted",
    "customer_notified": true
  }
}
```

### 5.5 Upload Receipt

```http
POST /api/v1/orders/{id}/receipt
Content-Type: multipart/form-data
Authorization: Bearer <token>

{
  "receipt_type": "single",         // "single" | "multiple" | "handwritten" | "manual"
  "photos": [<file>, <file>],       // photos for receipt types
  "manual_items": [                  // only for "manual" receipt_type
    { "name": "Rice", "quantity": 5, "price": 15000 }
  ]
}

Response 200:
{
  "success": true,
  "data": {
    "receipt_id": "uuid",
    "status": "verified",
    "final_amounts": {
      "item_cost": 28500,
      "service_fee": 2850,
      "delivery_fee": 3500,
      "total": 34850
    },
    "variance": {
      "percentage": -4.5,
      "customer_approval_required": false
    }
  }
}
```

### 5.6 Approve Price Variance

```http
POST /api/v1/orders/{id}/approve-variance
Authorization: Bearer <token>

{
  "approved": true
}

Response 200:
{
  "success": true,
  "data": {
    "status": "receipt_verified",
    "final_total": 42000
  }
}
```

### 5.7 Get Order Tracking

```http
GET /api/v1/orders/{id}/tracking
Authorization: Bearer <token>

Response 200:
{
  "success": true,
  "data": {
    "status": "in_delivery",
    "shopper_location": {
      "latitude": -6.7890,
      "longitude": 39.2050,
      "updated_at": "2026-07-24T11:15:00+03:00"
    },
    "eta": "12 min",
    "delivery_address": {
      "latitude": -6.7924,
      "longitude": 39.2083,
      "address_text": "123 Mikocheni Street"
    }
  }
}
```

---

## 6. Payment API

### 6.1 Pre-Authorise Payment

```http
POST /api/v1/payments/pre-auth
Content-Type: application/json
Authorization: Bearer <token>

{
  "order_id": "uuid",
  "amount": 36500,
  "payment_method": "mpesa",
  "customer_id": "uuid"
}

Response 200:
{
  "success": true,
  "data": {
    "payment_id": "uuid",
    "status": "authorized",
    "hold_reference": "mpesa-txn-ref-123",
    "hold_amount": 36500,
    "expires_at": "2026-07-25T10:30:00+03:00"
  }
}
```

### 6.2 Capture Payment

```http
POST /api/v1/payments/{id}/capture
Content-Type: application/json
Authorization: Bearer <token>

{
  "final_amount": 34850
}

Response 200:
{
  "success": true,
  "data": {
    "payment_id": "uuid",
    "status": "captured",
    "captured_amount": 34850,
    "capture_reference": "mpesa-capture-ref-456"
  }
}
```

### 6.3 Process Refund

```http
POST /api/v1/payments/{id}/refund
Content-Type: application/json
Authorization: Bearer <token>

{
  "amount": 5000,
  "reason": "item_damage",
  "dispute_id": "uuid",
  "admin_id": "uuid"          // required for manual refunds
}

Response 200:
{
  "success": true,
  "data": {
    "payment_id": "uuid",
    "status": "refunded",
    "refund_amount": 5000,
    "refund_reference": "mpesa-refund-ref-789"
  }
}
```

### 6.4 Get Payment Status

```http
GET /api/v1/payments/{id}
Authorization: Bearer <token>

Response 200:
{
  "success": true,
  "data": {
    "payment_id": "uuid",
    "order_id": "uuid",
    "status": "settled",
    "pre_auth_amount": 36500,
    "final_amount": 34850,
    "refunded_amount": 0,
    "shopper_payout": 6000,
    "platform_revenue": 2850,
    "transactions": [
      { "type": "authorization", "amount": 36500, "status": "completed", "timestamp": "..." },
      { "type": "capture", "amount": 34850, "status": "completed", "timestamp": "..." },
      { "type": "settlement", "amount": 6000, "status": "completed", "timestamp": "..." }
    ]
  }
}
```

---

## 7. Delivery API

### 7.1 Confirm Arrival

```http
POST /api/v1/deliveries/{id}/arrive
Authorization: Bearer <token>

{
  "latitude": -6.7924,
  "longitude": 39.2083,
  "gps_accuracy": 8        // meters
}

Response 200:
{
  "success": true,
  "data": {
    "status": "arrived",
    "gps_match": true,
    "distance_from_address_m": 12,
    "customer_notified": true
  }
}
```

### 7.2 Confirm Delivery

```http
POST /api/v1/deliveries/{id}/confirm
Authorization: Bearer <token>

{
  "delivery_photo": <base64_image>,
  "recipient_type": "customer",      // "customer" | "authorized_recipient" | "safe_drop"
  "recipient_name": "Aisha Mohamed", // optional — for authorized_recipient
  "recipient_relationship": "self",  // optional
  "customer_confirmed": true         // true if customer tapped confirm
}

Response 200:
{
  "success": true,
  "data": {
    "status": "delivered",
    "delivery_time_sec": 420,
    "inspection_window_min": 5,
    "inspection_category": "fresh_produce"
  }
}
```

### 7.3 Report Delay

```http
POST /api/v1/deliveries/{id}/report-delay
Authorization: Bearer <token>

{
  "reason": "heavy_traffic",     // "traffic" | "road_closure" | "weather" | "other"
  "estimated_additional_min": 15,
  "note": "Accident on Bagamoyo Road"
}

Response 200:
{
  "success": true,
  "data": {
    "new_eta_min": 20,
    "customer_notified": true
  }
}
```

### 7.4 Get Delivery ETA

```http
GET /api/v1/deliveries/{id}/eta
Authorization: Bearer <token>

Response 200:
{
  "success": true,
  "data": {
    "eta_min": 12,
    "distance_remaining_km": 2.4,
    "predicted_at": "2026-07-24T11:15:00+03:00",
    "delay_status": "on_time"    // "on_time" | "slightly_delayed" | "significantly_delayed"
  }
}
```

---

## 8. Dispute API

### 8.1 Create Dispute

```http
POST /api/v1/disputes
Content-Type: application/json
Authorization: Bearer <token>

{
  "order_id": "uuid",
  "type": "item_issue",               // "item_issue" | "cancellation" | "behaviour" | "payment"
  "severity": "minor",                // "minor" | "moderate" | "major"
  "description": "One tomato was damaged",
  "evidence_photos": [<base64_image>],
  "reported_by": "customer"
}

Response 201:
{
  "success": true,
  "data": {
    "id": "uuid",
    "status": "under_review",
    "resolution_path": "automated",
    "estimated_resolution_time": "1 hour",
    "created_at": "2026-07-24T11:30:00+03:00"
  }
}
```

### 8.2 Add Evidence

```http
POST /api/v1/disputes/{id}/evidence
Content-Type: multipart/form-data
Authorization: Bearer <token>

{
  "photos": [<file>, <file>],
  "description": "Additional photo showing the damaged item"
}
```

### 8.3 Get Dispute Status

```http
GET /api/v1/disputes/{id}
Authorization: Bearer <token>

Response 200:
{
  "success": true,
  "data": {
    "id": "uuid",
    "order_id": "uuid",
    "status": "decision",
    "type": "item_issue",
    "severity": "minor",
    "decision": {
      "outcome": "partial_refund",
      "refund_amount": 1500,
      "goodwill_amount": 0,
      "rationale": "Item value refunded per minor issue policy. No delivery adjustment applied.",
      "decided_by": "system",
      "decided_at": "2026-07-24T11:35:00+03:00"
    },
    "created_at": "2026-07-24T11:30:00+03:00",
    "resolved_at": null
  }
}
```

### 8.4 Escalate Dispute

```http
POST /api/v1/disputes/{id}/escalate
Authorization: Bearer <token>

{
  "reason": "not_satisfied_with_automated_resolution"
}

Response 200:
{
  "success": true,
  "data": {
    "status": "under_review",
    "assigned_to": "ops_admin",
    "escalated_at": "2026-07-24T11:40:00+03:00"
  }
}
```

---

## 9. Rating API

### 9.1 Submit Rating

```http
POST /api/v1/ratings
Content-Type: application/json
Authorization: Bearer <token>

{
  "order_id": "uuid",
  "rated_user_id": "uuid",           // shopper or customer being rated
  "rating_type": "customer_to_shopper",
  "score": 4,
  "feedback": "Great service, very helpful!",
  "criteria": {
    "item_accuracy": 5,
    "item_quality": 4,
    "timeliness": 4,
    "communication": 5,
    "professionalism": 5
  }
}

Response 201:
{
  "success": true,
  "data": {
    "id": "uuid",
    "status": "submitted",
    "visible_after": "2026-07-27T11:30:00+03:00"   // 72 hours
  }
}
```

### 9.2 Get Shopper Ratings

```http
GET /api/v1/shoppers/{id}/ratings?page=1&per_page=20
Authorization: Bearer <token>

Response 200:
{
  "success": true,
  "data": {
    "average": 4.5,
    "total_count": 87,
    "distribution": {
      "5": 52,
      "4": 28,
      "3": 5,
      "2": 2,
      "1": 0
    },
    "recent": [
      { "score": 5, "feedback": "Excellent!", "created_at": "..." }
    ]
  }
}
```

---

## 10. Notification API

### 10.1 Register Push Token

```http
POST /api/v1/notifications/register-device
Authorization: Bearer <token>

{
  "push_token": "fcm-token-here",
  "platform": "android",
  "device_id": "fingerprint-hash"
}

Response 200:
{
  "success": true,
  "data": { "status": "registered" }
}
```

### 10.2 Get Notification History

```http
GET /api/v1/notifications?page=1&per_page=50
Authorization: Bearer <token>

Response 200:
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "type": "order_status",
      "title": "Order Accepted",
      "body": "Juma has accepted your order!",
      "read": false,
      "created_at": "2026-07-24T10:30:45+03:00"
    }
  ]
}
```

### 10.3 Mark Notification Read

```http
POST /api/v1/notifications/{id}/read
Authorization: Bearer <token>

Response 200:
{
  "success": true,
  "data": { "status": "read", "read_at": "2026-07-24T11:00:00+03:00" }
}
```

---

## 11. Admin API

### 11.1 Get Dashboard Metrics

```http
GET /api/v1/admin/metrics
Authorization: Bearer <token>    // Admin role required

Response 200:
{
  "success": true,
  "data": {
    "gmv_today": 1250000,
    "gmv_week": 8750000,
    "active_shoppers": 234,
    "active_orders": 45,
    "fulfillment_rate": 96.5,
    "avg_assignment_time_sec": 18,
    "dispute_rate": 0.8,
    "pending_applications": 12,
    "open_disputes": 3
  }
}
```

### 11.2 List Pending Shopper Applications

```http
GET /api/v1/admin/shoppers/pending?page=1&per_page=20
Authorization: Bearer <token>

Response 200:
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "name": "Juma Ali",
      "phone": "255712345678",
      "transport_type": "motorcycle",
      "identity_status": "pending_review",
      "documents_status": "submitted",
      "submitted_at": "2026-07-23T14:00:00+03:00"
    }
  ]
}
```

### 11.3 Approve/Reject Shopper

```http
POST /api/v1/admin/shoppers/{id}/approve
Authorization: Bearer <token>

{
  "notes": "ID verified. Documents valid. Approved."
}

// OR

POST /api/v1/admin/shoppers/{id}/reject
{
  "reason": "identity_verification_failed",
  "notes": "ID photo does not match selfie"
}
```

### 11.4 Get Dispute Queue

```http
GET /api/v1/admin/disputes/queue?status=open&priority=urgent
Authorization: Bearer <token>

Response 200:
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "order_id": "uuid",
      "type": "behaviour",
      "severity": "high",
      "reported_by": "customer",
      "age_hours": 2,
      "assigned_to": null,
      "status": "under_review"
    }
  ]
}
```

### 11.5 Resolve Dispute

```http
POST /api/v1/admin/disputes/{id}/resolve
Authorization: Bearer <token>

{
  "decision": "partial_refund",
  "refund_amount": 5000,
  "goodwill_amount": 0,
  "rationale": "Moderate issue — affected items refunded plus 10% delivery adjustment.",
  "notes_internal": "Shopper warned about quality check."
}
```

### 11.6 Zone Management

```http
GET /api/v1/admin/zones
Authorization: Bearer <token>

POST /api/v1/admin/zones
{
  "name": "Kariakoo",
  "city": "Dar es Salaam",
  "boundary_coordinates": [ ... ],
  "max_assignment_radius_km": 6,
  "operating_hours_start": "06:00",
  "operating_hours_end": "22:00",
  "base_delivery_fee": 1500,
  "per_km_rate": 500,
  "status": "active"
}

PATCH /api/v1/admin/zones/{id}
{
  "per_km_rate": 600
}
```

### 11.7 Generate Report

```http
POST /api/v1/admin/reports/generate
Authorization: Bearer <token>

{
  "type": "monthly_financial",
  "month": "2026-07",
  "format": "csv"      // "csv" | "pdf"
}

Response 200:
{
  "success": true,
  "data": {
    "download_url": "https://...",
    "expires_at": "2026-07-25T11:00:00+03:00"
  }
}
```

---

## 12. Internal Service API

These endpoints are for service-to-service communication within the backend. They are not exposed through the API Gateway to public clients.

### 12.1 Assignment Score Calculation

```http
POST /internal/v1/assignments/calculate-score
Content-Type: application/json
Internal-Auth: <service-api-key>

{
  "zone_id": "uuid",
  "market_id": "uuid",
  "exclude_shopper_id": null
}

Response 200:
{
  "success": true,
  "data": {
    "ranked_shoppers": [
      {
        "shopper_id": "uuid",
        "score": 85.2,
        "breakdown": {
          "distance_score": 35.0,
          "acceptance_score": 18.5,
          "completion_score": 14.2,
          "rating_score": 9.1,
          "workload_score": 8.4,
          "activity_score": 0,
          "zone_priority_score": 0
        }
      }
    ],
    "calculation_time_ms": 45
  }
}
```

### 12.2 Process Shopper Settlement

```http
POST /internal/v1/payments/process-settlement
Internal-Auth: <service-api-key>

{
  "batch_size": 100
}

Response 200:
{
  "success": true,
  "data": {
    "processed": 98,
    "failed": 2,
    "total_amount": 585000,
    "settlement_time_ms": 45000
  }
}
```

### 12.3 Archive Orders

```http
POST /internal/v1/orders/archive
Internal-Auth: <service-api-key>

{
  "archive_before": "2026-04-24T00:00:00+03:00",   // 90 days ago
  "batch_size": 500
}
```

---

## 13. WebSocket API

### 13.1 Order Tracking (Customer)

```http
WebSocket: wss://api.urbanshopper.co.tz/api/v1/ws/orders/{orderId}/tracking
Token: <jwt_token>
```

**Messages received by client:**

```json
// Shopper GPS update (during In Delivery)
{
  "type": "gps_update",
  "data": {
    "latitude": -6.7890,
    "longitude": 39.2050,
    "speed_kmh": 25,
    "bearing": 180
  }
}

// ETA update
{
  "type": "eta_update",
  "data": {
    "eta_min": 12,
    "previous_eta_min": 15,
    "reason": "traffic"
  }
}

// Order status change
{
  "type": "status_change",
  "data": {
    "from": "receipt_verified",
    "to": "in_delivery",
    "timestamp": "2026-07-24T11:00:00+03:00"
  }
}
```

### 13.2 Offer Countdown (Shopper)

```http
WebSocket: wss://api.urbanshopper.co.tz/api/v1/ws/offers/{offerId}/countdown
Token: <jwt_token>
```

**Messages received by client:**

```json
// Timer tick (every second)
{
  "type": "countdown",
  "data": {
    "seconds_remaining": 25
  }
}

// Offer accepted by another shopper (race condition — shouldn't happen but handle)
{
  "type": "offer_withdrawn",
  "data": {
    "reason": "order_already_accepted"
  }
}
```

### 13.3 Admin Dashboard (Live)

```http
WebSocket: wss://api.urbanshopper.co.tz/api/v1/ws/admin/dashboard
Token: <admin_jwt_token>
```

**Messages received by client:**

```json
// Metrics update (every 30 seconds)
{
  "type": "metrics_update",
  "data": {
    "gmv_today": 1270000,
    "active_orders": 47,
    "active_shoppers": 236
  }
}

// New event alert
{
  "type": "alert",
  "data": {
    "severity": "urgent",
    "category": "dispute",
    "message": "New behavioural complaint filed — immediate review required",
    "action_url": "/disputes/uuid"
  }
}
```

---

*This document is Phase 10 of the Urban Shopper Platform specification. It defines the complete API surface for all platform services and is intended for use by frontend developers, integration partners, and test automation.*
