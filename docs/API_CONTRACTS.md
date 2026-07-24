# Urban Shopper Platform - API Contracts and Interfaces

> **⚠️ NOTE: Source of Truth is `10-api-specification.md`**  
> This document provides API contract patterns for development reference. For the complete, detailed API specification with all endpoints, request/response formats, authentication flows, and error codes, refer to **`10-api-specification.md`** (Phase 10). That document is authoritative. This file summarises communication patterns for quick team reference.
> **Status:** Active  
> **Date:** 2026-07-24  
> **Version:** 1.0  
> **Purpose:** Define API contracts, interfaces, and communication patterns between services

---

## 1. Overview

This document defines the API contracts and interfaces for the Urban Shopper Platform microservices. It serves as the source of truth for service-to-service communication specifications.

## 2. API Design Principles

### 2.1 REST API Principles
- **Resource-oriented:** URLs represent resources, not actions
- **HTTP methods:** GET (read), POST (create), PUT (update), PATCH (partial update), DELETE (remove)
- **Stateless:** Each request contains all necessary information
- **HATEOAS:** Include links to related resources where appropriate
- **Versioning:** API version in URL path (`/api/v1/...`)

### 2.2 Event-Driven Principles
- **Event naming:** Past tense (`OrderCreated`, `PaymentProcessed`)
- **Event schema:** Include event ID, timestamp, source, payload
- **Idempotency:** Events should be processed idempotently
- **Schema evolution:** Backward compatible changes only

### 2.3 Error Handling
- **HTTP status codes:** Standard codes (200, 400, 401, 403, 404, 500)
- **Error response format:** Consistent error object with code, message, details
- **Validation errors:** Detailed field-level validation errors

## 3. REST API Contracts

### 3.1 User Service API

#### 3.1.1 Customer Management
```
POST   /api/v1/customers/register     # Register new customer
POST   /api/v1/customers/login        # Login with OTP
GET    /api/v1/customers/{id}         # Get customer profile
PUT    /api/v1/customers/{id}         # Update customer profile
POST   /api/v1/customers/{id}/verify  # Verify customer identity
```

**Register Customer Request:**
```json
{
  "phone": "+255712345678",
  "name": "Aisha Mohamed",
  "language": "sw",
  "notification_preferences": {
    "push": true,
    "sms": true,
    "in_app": true
  }
}
```

**Register Customer Response:**
```json
{
  "id": "uuid",
  "name": "Aisha Mohamed",
  "phone": "255712345678",
  "status": "active",
  "trust_score": 50,
  "created_at": "2026-07-24T10:30:00+03:00"
}
```

#### 3.1.2 Shopper Management
```
POST   /api/v1/shoppers/register      # Register new shopper
POST   /api/v1/shoppers/{id}/verify   # Submit verification documents
GET    /api/v1/shoppers/{id}          # Get shopper profile
PUT    /api/v1/shoppers/{id}/status   # Update shopper status
GET    /api/v1/shoppers/online        # Get online shoppers in zone
```

**Shopper Registration Request:**
```json
{
  "phoneNumber": "+255712345679",
  "fullName": "Jane Smith",
  "nationalId": "1234567890123456",
  "dateOfBirth": "1990-01-01",
  "vehicleType": "MOTORCYCLE",
  "bankAccount": {
    "bankName": "CRDB",
    "accountNumber": "1234567890",
    "accountName": "Jane Smith"
  },
  "documents": {
    "nationalIdFront": "base64...",
    "nationalIdBack": "base64...",
    "profilePhoto": "base64..."
  }
}
```

### 3.2 Order Service API

#### 3.2.1 Order Management
```
POST   /api/v1/orders                 # Create new order
GET    /api/v1/orders/{id}            # Get order details
PUT    /api/v1/orders/{id}/status     # Update order status
GET    /api/v1/customers/{id}/orders  # Get customer orders
GET    /api/v1/shoppers/{id}/orders   # Get shopper orders
```

**Create Order Request:**
```json
{
  "customerId": "cust_123456789",
  "deliveryLocation": {
    "latitude": -6.7924,
    "longitude": 39.2083,
    "address": "123 Main Street, Dar es Salaam",
    "notes": "Ring bell twice"
  },
  "items": [
    {
      "name": "Rice",
      "quantity": 2,
      "unit": "kg",
      "category": "GROCERIES",
      "notes": "Basmati rice preferred",
      "maxPrice": 5000,
      "substitutionPreference": "CONTACT_ME"
    },
    {
      "name": "Tomatoes",
      "quantity": 1,
      "unit": "kg",
      "category": "PRODUCE",
      "notes": "Fresh and ripe",
      "maxPrice": 2000,
      "substitutionPreference": "BEST_MATCH"
    }
  ],
  "paymentMethod": "MPESA",
  "deliveryTimePreference": "ASAP"
}
```

**Order Response:**
```json
{
  "orderId": "ord_987654321",
  "status": "AWAITING_PAYMENT_VERIFICATION",
  "estimatedTotal": 15000,
  "deliveryFee": 3000,
  "serviceFee": 1200,
  "paymentAuthRequired": true,
  "paymentAuthUrl": "/api/v1/payments/auth/order_987654321",
  "createdAt": "2026-07-24T10:30:00Z"
}
```

### 3.3 Assignment Engine API

#### 3.3.1 Shopper Assignment
```
POST   /api/v1/assignment/offer       # Offer order to shopper
POST   /api/v1/assignment/accept      # Shopper accepts offer
POST   /api/v1/assignment/decline     # Shopper declines offer
GET    /api/v1/assignment/status/{orderId}  # Get assignment status
```

**Offer Order Request:**
```json
{
  "orderId": "ord_987654321",
  "customerLocation": {
    "latitude": -6.7924,
    "longitude": 39.2083
  },
  "marketLocation": {
    "latitude": -6.7950,
    "longitude": 39.2100
  },
  "estimatedShoppingTime": 30,
  "estimatedDeliveryTime": 15,
  "totalValue": 15000,
  "priority": "NORMAL"
}
```

### 3.4 Payment Service API

#### 3.4.1 Payment Processing
```
POST   /api/v1/payments/preauth       # Pre-authorize payment
POST   /api/v1/payments/capture       # Capture payment
POST   /api/v1/payments/refund        # Process refund
POST   /api/v1/payments/settle        # Settle shopper payout
GET    /api/v1/payments/status/{transactionId}  # Get payment status
```

**Pre-authorize Request:**
```json
{
  "orderId": "ord_987654321",
  "customerId": "cust_123456789",
  "amount": 15000,
  "currency": "TZS",
  "paymentMethod": "MPESA",
  "phoneNumber": "+255712345678",
  "description": "Urban Shopper Order #987654321"
}
```

## 4. Event Contracts

### 4.1 Event Schema
All events follow this base structure:
```json
{
  "eventId": "evt_123456789",
  "eventType": "OrderCreated",
  "eventVersion": "1.0",
  "timestamp": "2026-07-24T10:30:00Z",
  "source": "order-service",
  "correlationId": "corr_987654321",
  "payload": {
    // Event-specific payload
  }
}
```

### 4.2 Core Events

#### 4.2.1 Order Domain Events
```json
{
  "eventType": "OrderCreated",
  "payload": {
    "orderId": "ord_987654321",
    "customerId": "cust_123456789",
    "status": "CREATED",
    "totalAmount": 15000,
    "items": [
      {
        "itemId": "item_001",
        "name": "Rice",
        "quantity": 2,
        "unit": "kg"
      }
    ]
  }
}

{
  "eventType": "OrderStatusChanged",
  "payload": {
    "orderId": "ord_987654321",
    "previousStatus": "CREATED",
    "newStatus": "AWAITING_PAYMENT_VERIFICATION",
    "changedBy": "system",
    "reason": "Order submitted"
  }
}

{
  "eventType": "PaymentVerified",
  "payload": {
    "orderId": "ord_987654321",
    "paymentId": "pay_123456789",
    "amount": 15000,
    "status": "PRE_AUTHORIZED"
  }
}
```

#### 4.2.2 Assignment Events
```json
{
  "eventType": "OrderReadyForAssignment",
  "payload": {
    "orderId": "ord_987654321",
    "customerLocation": {
      "latitude": -6.7924,
      "longitude": 39.2083
    },
    "marketLocation": {
      "latitude": -6.7950,
      "longitude": 39.2100
    },
    "zoneId": "zone_dar_mikocheni"
  }
}

{
  "eventType": "ShopperOfferedOrder",
  "payload": {
    "orderId": "ord_987654321",
    "shopperId": "shop_123456789",
    "assignmentScore": 85.5,
    "offerExpiresAt": "2026-07-24T10:30:30Z"
  }
}

{
  "eventType": "ShopperAcceptedOrder",
  "payload": {
    "orderId": "ord_987654321",
    "shopperId": "shop_123456789",
    "acceptedAt": "2026-07-24T10:30:15Z"
  }
}
```

#### 4.2.3 Payment Events
```json
{
  "eventType": "PaymentPreAuthorized",
  "payload": {
    "paymentId": "pay_123456789",
    "orderId": "ord_987654321",
    "amount": 15000,
    "status": "PRE_AUTHORIZED",
    "providerReference": "MPESA_REF_123456"
  }
}

{
  "eventType": "PaymentCaptured",
  "payload": {
    "paymentId": "pay_123456789",
    "orderId": "ord_987654321",
    "amount": 15000,
    "status": "CAPTURED",
    "capturedAt": "2026-07-24T11:45:00Z"
  }
}

{
  "eventType": "ShopperPayoutSettled",
  "payload": {
    "payoutId": "payout_123456789",
    "shopperId": "shop_123456789",
    "amount": 10800,
    "orderIds": ["ord_987654321"],
    "settledAt": "2026-07-26T10:30:00Z"
  }
}
```

## 5. WebSocket Contracts

### 5.1 Connection Establishment
```
Client connects to: wss://api.urbanshopper.tz/ws

Authentication: Include JWT token in query parameter
wss://api.urbanshopper.tz/ws?token=<jwt_token>
```

### 5.2 Channels

#### 5.2.1 Order Tracking Channel
**Subscribe:**
```json
{
  "action": "subscribe",
  "channel": "order_tracking",
  "orderId": "ord_987654321"
}
```

**Messages:**
```json
{
  "channel": "order_tracking",
  "orderId": "ord_987654321",
  "event": "status_update",
  "data": {
    "status": "SHOPPING",
    "updatedAt": "2026-07-24T11:00:00Z",
    "shopperLocation": {
      "latitude": -6.7950,
      "longitude": 39.2100
    },
    "itemsFound": 3,
    "itemsTotal": 5
  }
}
```

#### 5.2.2 Shopper Offer Channel
**Subscribe (Shopper):**
```json
{
  "action": "subscribe",
  "channel": "shopper_offers",
  "shopperId": "shop_123456789"
}
```

**Offer Message:**
```json
{
  "channel": "shopper_offers",
  "event": "new_offer",
  "data": {
    "offerId": "off_123456789",
    "orderId": "ord_987654321",
    "customerLocation": {
      "latitude": -6.7924,
      "longitude": 39.2083
    },
    "marketLocation": {
      "latitude": -6.7950,
      "longitude": 39.2100
    },
    "estimatedEarnings": 3500,
    "expiresIn": 30
  }
}
```

#### 5.2.3 Chat Channel
**Subscribe:**
```json
{
  "action": "subscribe",
  "channel": "chat",
  "orderId": "ord_987654321",
  "participantId": "cust_123456789"
}
```

**Chat Message:**
```json
{
  "channel": "chat",
  "event": "new_message",
  "data": {
    "messageId": "msg_123456789",
    "orderId": "ord_987654321",
    "senderId": "cust_123456789",
    "senderType": "CUSTOMER",
    "content": "Can you get ripe avocados if available?",
    "timestamp": "2026-07-24T11:05:00Z"
  }
}
```

## 6. Error Response Format

### 6.1 Standard Error Response
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input parameters",
    "details": [
      {
        "field": "phoneNumber",
        "message": "Phone number must be in E.164 format"
      },
      {
        "field": "email",
        "message": "Email must be a valid email address"
      }
    ],
    "timestamp": "2026-07-24T10:30:00Z",
    "requestId": "req_123456789"
  }
}
```

### 6.2 Common Error Codes
| Code | HTTP Status | Description |
|------|-------------|-------------|
| `VALIDATION_ERROR` | 400 | Input validation failed |
| `AUTHENTICATION_FAILED` | 401 | Invalid credentials or token |
| `FORBIDDEN` | 403 | Insufficient permissions |
| `RESOURCE_NOT_FOUND` | 404 | Requested resource doesn't exist |
| `CONFLICT` | 409 | Resource conflict (e.g., duplicate) |
| `RATE_LIMITED` | 429 | Too many requests |
| `INTERNAL_ERROR` | 500 | Internal server error |
| `SERVICE_UNAVAILABLE` | 503 | Service temporarily unavailable |

## 7. API Versioning Strategy

### 7.1 URL Path Versioning
```
/api/v1/orders     # Current stable version
/api/v2/orders     # Future version (when needed)
```

### 7.2 Breaking Changes Policy
1. **Major version increment** for breaking changes
2. **Deprecation period:** 6 months for old versions
3. **Backward compatibility:** Maintain old endpoints during deprecation
4. **Client notification:** API changelog and deprecation headers

## 8. Testing Contracts

### 8.1 Contract Testing
- **Consumer-driven contracts:** Each service defines expected provider behavior
- **Pact testing:** Contract files shared between consumer and provider
- **Schema validation:** JSON Schema for all request/response formats

### 8.2 Example Contract Test
```yaml
# contract/user-service-customer-registration.yml
provider: user-service
consumer: customer-app
request:
  method: POST
  path: /api/v1/customers/register
  headers:
    Content-Type: application/json
  body:
    phoneNumber: string
    fullName: string
    email: string?
    password: string
response:
  status: 201
  headers:
    Content-Type: application/json
  body:
    customerId: string
    status: string
    otpSent: boolean
    message: string
```

## 9. Next Steps

1. **Generate OpenAPI/Swagger specifications** for each service
2. **Implement contract testing** using Pact or similar
3. **Create API client libraries** for each service
4. **Set up API documentation portal** with interactive examples
5. **Establish API governance process** for changes and approvals

---

**Note:** These contracts will be implemented during Phase 0 and Phase 1 development. All services must adhere to these specifications for interoperability.