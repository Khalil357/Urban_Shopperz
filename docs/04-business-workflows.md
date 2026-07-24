# Urban Shopper Platform — Business Workflows

> **⚠️ SUPERSEDED — Replaced by `05-business-process-models.md`**  
> This document contains an earlier workflow draft. All business processes have been refined and formalised as BPMN process models in Phase 5 (`05-business-process-models.md`). Refer to that document for the current, complete process specifications.

> **Document Type:** Business Analysis  
> **Status:** Complete  
> **Date:** 2026-07-24  
> **Version:** 1.0  
> **Cross-Reference:** Business Rules — `03-business-rules.md`, Vision & Product Definition — `02-vision-product-definition.md`

---

## Table of Contents

1. [Customer Workflows](#1-customer-workflows)
   - [1.1 Registration & Onboarding](#11-customer-registration--onboarding)
   - [1.2 Submit Shopping Request](#12-submit-shopping-request)
   - [1.3 Track Order & Communicate](#13-track-order--communicate)
   - [1.4 Receive Delivery & Complete Order](#14-receive-delivery--complete-order)
   - [1.5 Cancel Order](#15-cancel-order)
   - [1.6 Request Refund / Dispute](#16-request-refund--dispute)
2. [Shopper Workflows](#2-shopper-workflows)
   - [2.1 Registration, Verification & Onboarding](#21-shopper-registration-verification--onboarding)
   - [2.2 Go Online & Receive Offers](#22-go-online--receive-offers)
   - [2.3 Accept Order & Navigate to Market](#23-accept-order--navigate-to-market)
   - [2.4 Shop Items & Handle Substitutions](#24-shop-items--handle-substitutions)
   - [2.5 Checkout, Receipt Upload & Payment](#25-checkout-receipt-upload--payment)
   - [2.6 Deliver Order to Customer](#26-deliver-order-to-customer)
   - [2.7 Receive Earnings & Review Rating](#27-receive-earnings--review-rating)
3. [Administrator Workflows](#3-administrator-workflows)
   - [3.1 Shopper Vetting & Approval](#31-shopper-vetting--approval)
   - [3.2 Dispute Resolution](#32-dispute-resolution)
   - [3.3 Fraud Alert Investigation](#33-fraud-alert-investigation)
   - [3.4 Emergency Order Intervention](#34-emergency-order-intervention)
   - [3.5 Platform Monitoring & Reporting](#35-platform-monitoring--reporting)
4. [Future Vendor Workflow (V2 Mock-up)](#4-future-vendor-workflow-v2-mock-up)
5. [Future Corporate Client Workflow (V2 Mock-up)](#5-future-corporate-client-workflow-v2-mock-up)

---

## 1. Customer Workflows

### 1.1 Customer Registration & Onboarding

| Aspect | Detail |
|--------|--------|
| **Actors** | Prospective Customer, Platform System |
| **Trigger** | Customer downloads the app and taps "Register" |
| **Preconditions** | Customer has a smartphone with internet connectivity and a valid Tanzanian mobile phone number with active M-Pesa/Mixx account |

**Main Flow:**

| Step | Description | Business Rules |
|------|-------------|----------------|
| 1 | Customer opens the app and selects "Create Account" | A-001 |
| 2 | System displays registration form: full name, phone number, email (optional), password | A-001 |
| 3 | Customer enters required information and accepts Terms of Service | L-007 |
| 4 | System sends SMS OTP to the provided phone number | A-003 |
| 5 | Customer enters OTP within 5 minutes | A-003 |
| 6 | System verifies OTP and validates uniqueness of phone number | I-001 |
| 7 | System prompts customer to select language preference (Swahili/English) and notification preferences | A-007 |
| 8 | System creates the account with "Pending Phone Verification" status (now verified) | A-003 |
| 9 | System presents onboarding tutorial (3 screens: how to order, payment, tracking) | — |
| 10 | System presents first-time offer (50% off delivery fee) | F-007 |
| 11 | Customer completes onboarding and is directed to the home screen | — |

**Alternative Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 4a | OTP not received after 60 seconds | Customer may request resend (max 3 attempts within 30 minutes). System re-sends OTP via SMS + voice call fallback. |
| 5a | OTP expired (valid for 5 minutes) | System prompts for resend. New OTP sent. |
| 6a | Phone number already registered | System informs customer that the number is already registered. Offers password reset or account recovery options. |
| 8a | Under 18 attempted registration | System displays message: "You must be 18 or older to use Urban Shopper." Registration blocked. |

**Exception Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 4-5 | SMS gateway failure (OTP not deliverable) | System logs the failure and retries via alternative SMS provider. After 3 failed attempts, system displays "Registration temporarily unavailable" with retry option. |
| 6b | Duplicate account detected via device fingerprinting | System flags for admin review. Account created but placed in "Pending Review" status with limited functionality (can browse but cannot order). |

**Post Conditions:**
- Customer account is created with status "Active"
- Customer can browse, search, and create orders
- Customer's first-order promotional discount is activated

---

### 1.2 Submit Shopping Request

| Aspect | Detail |
|--------|--------|
| **Actors** | Registered Customer, Platform System |
| **Trigger** | Customer taps "New Order" on the home screen |
| **Preconditions** | Customer is logged in with a verified account. Customer has sufficient mobile money balance (or has enabled COD). Customer has fewer than 3 active orders. |

**Main Flow:**

| Step | Description | Business Rules |
|------|-------------|----------------|
| 1 | Customer selects delivery location (pin-drop on map + text address entry) | E-005 |
| 2 | Customer enters items one by one: item name, quantity, optional notes. System provides predictive text for common items. | D-002 |
| 3 | System displays estimated cost breakdown: item estimate, service fee (15%), delivery fee (distance-based) | D-003, F-001, F-002 |
| 4 | Customer selects delivery preference: ASAP or Scheduled (date/time) | D-014 |
| 5 | Customer selects payment method: Wallet (M-Pesa/Mixx) or Cash on Delivery | F-003, F-012 |
| 6 | Customer sets substitution preference per item: Best Match / Contact Me / No Substitutions | D-007 |
| 7 | Customer reviews order summary and taps "Submit Order" | D-003 |
| 8 | System places a pre-authorisation hold on customer's mobile money wallet for the estimated total | F-003 |
| 9 | System creates the order with status "Created" | D-004 |
| 10 | System begins the assignment process (see Shopper Workflow 2.2) | C-002, C-003 |
| 11 | Customer is redirected to the order tracking screen | — |

**Alternative Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 3a | Estimated total exceeds wallet balance and COD is selected | System allows submission with wallet hold for COD risk fee only. |
| 8a | Insufficient mobile money balance for pre-authorisation hold | System displays "Insufficient funds" message with options: (a) deposit via M-Pesa, (b) switch to COD (if eligible), (c) reduce order size. |
| 8b | Pre-authorisation API timeout | System retries up to 2 times with 5-second intervals. If all fail, order is not created and customer is asked to retry. |

**Exception Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 2 | Customer tries to add a prohibited item (keyword detected) | System blocks the item with message: "This item cannot be ordered through Urban Shopper." Item is not added to list. |
| 8c | Mobile money provider system offline | System notifies customer of payment provider outage. Stores order as draft. Customer can retry later. |
| 9 | System error prevents order creation | System logs error, notifies operations admin. Customer sees "Something went wrong. Please try again." Order not created. |

**Post Conditions:**
- Order is created with status "Created"
- Pre-authorisation hold is active on customer's wallet
- Customer sees order tracking screen
- Assignment engine is actively seeking a shopper

---

### 1.3 Track Order & Communicate

| Aspect | Detail |
|--------|--------|
| **Actors** | Customer, Shopper, Platform System |
| **Trigger** | Customer views the order tracking screen after order submission |
| **Preconditions** | Order exists in Created, Offered, Accepted, Shopping, or In Delivery status |

**Main Flow:**

| Step | Description | Business Rules |
|------|-------------|----------------|
| 1 | System displays current order status with visual indicator (status bar) | D-004 |
| 2 | When order is offered, system shows "Finding a shopper..." with countdown | C-003 |
| 3 | When order is accepted, system shows: shopper name, photo, rating, and ETA to market | B-001, H-003 |
| 4 | System displays shopper's real-time GPS location on map during "En Route to Market" and "In Delivery" phases | C-001 |
| 5 | When shopping starts, system shows item checklist with status per item (Found/Substituted/Not Available) | D-006 |
| 6 | Customer receives push notifications at each major status change | J-004 |
| 7 | Customer may open in-app chat with shopper throughout active order | J-001 |
| 8 | Customer may call shopper via masked call (if available) | J-002 |

**Alternative Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 2a | 30 seconds elapsed, shopper declined | System updates display: "Shopper declined. Finding next available shopper..." Relisting countdown. |
| 2b | 5 minutes elapsed, no shopper found | System displays order expiration options (D-010): expand radius, increase fee, or cancel. |
| 3a | Customer receives substitution request (Contact Me) | System displays substitution notification with shopper's suggested alternatives. Customer has 3 minutes to respond. | D-007 |
| 7a | Shopper does not respond to chat within 5 minutes | System sends a nudge to the shopper. After 10 minutes, support is notified. | J-005 |

**Post Conditions:**
- Customer is continuously updated on order progress
- Communication channel is open with assigned shopper
- When order completes, tracking screen transitions to delivery confirmation

---

### 1.4 Receive Delivery & Complete Order

| Aspect | Detail |
|--------|--------|
| **Actors** | Customer, Shopper, Platform System |
| **Trigger** | Shopper arrives at delivery location and marks "Arrived" |
| **Preconditions** | Order is in "In Delivery" status. Shopper GPS indicates shopper is at or near the delivery address. |

**Main Flow:**

| Step | Description | Business Rules |
|------|-------------|----------------|
| 1 | Customer receives notification: "Your shopper has arrived" | J-004 |
| 2 | Shopper calls customer via masked call to coordinate handover (if needed) | J-002 |
| 3 | Customer receives items from shopper | — |
| 4 | Shopper takes a photo of delivered items (with customer or delivery location) | E-002 |
| 5 | Customer confirms receipt in the app (taps "Items Received") | E-002 |
| 6 | System marks order as "Delivered" and starts the 15-minute inspection window | E-007 |
| 7 | Customer inspects items within 15-minute window | E-007 |
| 8 | Customer reports any issues OR confirms everything is correct | G-007 |
| 9 | After 15 minutes (or earlier confirmation), system marks order as "Completed" | E-007 |
| 10 | System processes final payment: release escrow (or complete mobile money transfer) | F-004, M-010 |
| 11 | Customer receives notification: order complete, final amount charged | — |
| 12 | System prompts customer to rate the shopper | H-001 |
| 13 | System prompts shopper to rate the customer | H-002 |

**Alternative Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 1a | Customer is not at delivery location | Customer contacts shopper via chat. If correctable (nearby), delivery proceeds. If significantly different, see Wrong Address procedure (E-005). |
| 3a | Customer unavailable at delivery | Shopper follows Unavailable Customer procedure (E-004): wait 10 min, call, chat, then leave at safe location or return items. |
| 6a | COD payment | Customer pays shopper the total amount in cash. Shopper confirms receipt in app. Payment verification happens on platform side. |
| 8a | Customer reports item issues | System enters Dispute workflow (Customer Workflow 1.6). |

**Exception Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 4 | Shopper cannot take photo (device camera issue) | Shopper may manually confirm delivery with a text note. Order is flagged for admin review. |
| 9 | Customer does not respond within 15-minute window | System automatically completes the order. Customer may still report issues via support. |
| 10 | Payment processing failure | System follows Failed Payment procedure (F-011): retry 2 times, then escalate. |

**Post Conditions:**
- Order status is "Completed"
- Customer's wallet is charged the final amount
- Shopper receives pending earnings credit
- Rating prompts are active (blind period)
- The 72-hour rating window begins

---

### 1.5 Cancel Order

| Aspect | Detail |
|--------|--------|
| **Actors** | Customer, Platform System, Shopper (if already assigned) |
| **Trigger** | Customer taps "Cancel Order" on the order tracking screen |
| **Preconditions** | Order exists in Created, Offered, Accepted, or Shopping status. Cancellation has not already been requested. |

**Main Flow:**

| Step | Description | Business Rules |
|------|-------------|----------------|
| 1 | Customer taps "Cancel Order" and selects a reason from the list | — |
| 2 | System checks current order status | D-004 |
| 3 | **If status is "Created" or "Offered"**: System immediately cancels with no fee. Releases pre-authorisation hold. | G-001 |
| 4 | **If status is "Accepted"** (shopper has accepted but not yet started): System calculates cancellation fee (50% of delivery fee). Displays fee to customer. Customer confirms. | G-002 |
| 5 | **If status is "Shopping"** (shopper is at market): System calculates cancellation fee (full delivery fee + 10% restocking fee). Displays fee. Customer confirms. | G-003 |
| 6 | Customer confirms cancellation with fee acknowledgement | — |
| 7 | System notifies the shopper of cancellation with compensation details | — |
| 8 | System processes refund minus cancellation fee (to wallet) | G-008 |
| 9 | System marks order as "Cancelled" with reason code | — |

**Alternative Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 3a | Customer has cancelled 3+ orders in the same status within 7 days | System flags account for abuse review. Cancellation still proceeds, but admin reviews pattern. |
| 4a | Shopper is more than 15 minutes into travel to market | Fee increases to 75% of delivery fee automatically. System explains the increase. |
| 5a | Shopper has already purchased items | Customer may choose to receive already-purchased items (full payment) or cancel with restocking fee. |

**Exception Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 6 | Customer refuses cancellation fee | Order continues. Customer may contact support to dispute the fee. |
| 8 | Refund processing fails | System logs failure and retries. Support is notified if failure persists beyond 2 hours. |

**Post Conditions:**
- Order is marked "Cancelled" with cancellation reason
- Customer receives refund (minus any applicable fees)
- Shopper receives compensation if applicable
- Pre-authorisation hold is released

---

### 1.6 Request Refund / Dispute

| Aspect | Detail |
|--------|--------|
| **Actors** | Customer, Platform Support System, Support Agent |
| **Trigger** | Customer reports an issue with a delivered order via the app |
| **Preconditions** | Order is in "Delivered" or "Completed" status. The issue is within the applicable reporting window. |

**Main Flow:**

| Step | Description | Business Rules |
|------|-------------|----------------|
| 1 | Customer selects the order and taps "Report Issue" | — |
| 2 | Customer selects issue type: Missing Item / Wrong Item / Damaged Item / Price Discrepancy / Other | G-007 |
| 3 | Customer uploads photo evidence and writes description | K-006 |
| 4 | System checks reporting window (2 hours from delivery for standard items, 15 min for perishables) | E-007 |
| 5 | If within window and claim value < 10,000 TZS with clear evidence → automated resolution: refund issued | L-008 |
| 6 | If above threshold or complex → system creates a support ticket and escalates to queue | L-008 |
| 7 | Support agent reviews case (evidence, chat logs, receipt, GPS history) | — |
| 8 | Agent makes decision: full refund, partial refund, or claim rejected | — |
| 9 | System notifies customer of decision with explanation | — |
| 10 | If refund approved: refund processed to wallet within 2 hours | G-008 |
| 11 | If rejected: customer may escalate for manual review (within 24-hour response) | G-009 |
| 12 | Case closed and logged for analytics | — |

**Alternative Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 4a | Outside reporting window (more than 2 hours) | Claim is initially rejected with explanation of the policy. Customer may still escalate for discretionary review (K-008). |
| 5a | Automated resolution determines no refund due | Case escalated to support agent for manual review anyway. |
| 10a | Refund to original mobile money number required (customer requests) | Within 24 hours if external transfer, within 2 hours if to wallet. |

**Exception Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 3 | Customer provides no evidence | Claim is still processed but may be rejected with request for evidence. |
| 8 | Agent cannot determine fault (inconclusive evidence) | Platform issues a 50% goodwill refund split between customer and shopper compensation. |
| 9 | Customer disputes agent decision | Case escalated to Operations Admin for final review. Admin decision is binding. |

**Post Conditions:**
- Dispute is resolved with a decision communicated to both parties
- Refund is processed (if applicable)
- Case is logged for analytics and quality monitoring
- Shopper record is updated (if shopper error found, rating impact applied)

---

## 2. Shopper Workflows

### 2.1 Shopper Registration, Verification & Onboarding

| Aspect | Detail |
|--------|--------|
| **Actors** | Prospective Shopper, Platform System, Operations Admin |
| **Trigger** | Prospective shopper downloads the shopper app and taps "Register as Shopper" |
| **Preconditions** | Applicant is 18+ years old, has a smartphone with GPS, has government-issued ID, has a mobile money account, has a means of transport |

**Main Flow:**

| Step | Description | Business Rules |
|------|-------------|----------------|
| 1 | Applicant downloads "Urban Shopper — Shopper App" and selects "Register" | B-001 |
| 2 | Applicant enters: full name, phone number, email (optional), home zone/neighbourhood | B-001 |
| 3 | System sends SMS OTP to verify phone number | A-003 |
| 4 | Applicant uploads photos of government-issued ID (front and back) | B-002 |
| 5 | Applicant takes a live selfie for facial matching | B-002 |
| 6 | Applicant selects transport type: Motorcycle / Bicycle / Car / Public Transport / Walking | B-009 |
| 7 | If motorised transport: applicant uploads driving licence, vehicle registration, insurance | B-009 |
| 8 | Applicant enters their M-Pesa/Mixx number for payouts | F-010 |
| 9 | Applicant consents to background check | B-003 |
| 10 | System submits application for review (status: "Pending Verification") | — |
| 11 | **Manual review** (see Admin Workflow 3.1): Admin reviews ID, facial match, documents | L-001 |
| 12 | If approved: system sends SMS notification and prompts onboarding training | B-004 |
| 13 | Applicant completes mandatory onboarding training (video + quiz) | B-004 |
| 14 | Applicant signs the Shopper Agreement (digital signature) | L-007 |
| 15 | System activates the shopper account (status: "Active") | — |
| 16 | Shopper is prompted to set availability hours and go online | B-005, B-013 |

**Alternative Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 5a | Facial matching fails (poor lighting, angle) | System prompts applicant to retake. After 3 failed attempts, application is queued for manual review. |
| 7a | Motorised documents expired or invalid | System identifies the specific document issue and asks applicant to resubmit. Application on hold until valid documents provided. |
| 11a | Application rejected (background check, document verification) | System sends rejection notification with reason. Applicant may appeal (B-012). |
| 13a | Onboarding quiz failed (score < 70%) | Applicant may retake the quiz up to 2 more times. After 3 failures, 7-day waiting period before reapplication. |

**Post Conditions:**
- Shopper account is created and activated
- Shopper is in "Inactive" status (offline)
- Shopper has completed onboarding training
- Shopper can go online and start receiving offers

---

### 2.2 Go Online & Receive Offers

| Aspect | Detail |
|--------|--------|
| **Actors** | Shopper, Platform System (Assignment Engine) |
| **Trigger** | Shopper taps "Go Online" in the shopper app |
| **Preconditions** | Shopper is verified, onboarded, and has no active orders. Shopper has GPS enabled. Shopper is within an active coverage zone. |

**Main Flow:**

| Step | Description | Business Rules |
|------|-------------|----------------|
| 1 | Shopper taps "Go Online" | B-005 |
| 2 | System sets shopper status to "Active" | B-005 |
| 3 | System begins receiving shopper's GPS coordinates every 10 seconds | C-001 |
| 4 | System displays shopper's current coverage zone and available metrics | C-007 |
| 5 | A new order is created by a customer within the shopper's zone | — |
| 6 | Assignment engine calculates distances from all Active shoppers to the market | C-002 |
| 7 | Engine identifies the nearest eligible shopper (this shopper) | C-002 |
| 8 | System sends a push notification + in-app alert with order details: item count, market location, estimated distance, estimated pay | C-003, J-003 |
| 9 | Shopper sees offer with 30-second countdown timer | C-003 |
| 10 | Shopper reviews the offer details | — |
| 11 | Shopper decides to Accept or Decline | — |

**Alternative Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 3a | GPS signal lost | System displays "GPS Lost" warning. Uses last known location (timestamped). After 2 minutes without GPS, automatically sets shopper to "Inactive" to prevent bad assignments. |
| 8a | Push notification not delivered within 5 seconds | System sends SMS fallback with offer summary | J-003 |
| 11a | No offers received for 30+ minutes while active in a high-demand zone | System sends shopper a suggestion to move to a higher-demand area (heat map shown in app). |
| 11b | Shopper manually goes offline | System sets status to "Inactive." Any pending active order is NOT affected — shopper must complete current order before going offline. |

**Post Conditions (if Accept):**
- Shopper is assigned to the order
- Order status changes to "Accepted"
- Shopper sees customer's delivery location
- Shopper can start navigating to market

**Post Conditions (if Decline/Timeout):**
- Shopper remains Active and eligible for next offer
- Shopper's acceptance rate metric is updated (B-011)
- Order is offered to the next nearest shopper (C-004)

---

### 2.3 Accept Order & Navigate to Market

| Aspect | Detail |
|--------|--------|
| **Actors** | Shopper, Platform System |
| **Trigger** | Shopper taps "Accept" on an order offer |
| **Preconditions** | Order exists in "Offered" status. Shopper is Active. Shopper's GPS is reporting. |

**Main Flow:**

| Step | Description | Business Rules |
|------|-------------|----------------|
| 1 | Shopper taps "Accept" | — |
| 2 | System validates shopper's GPS is within the delivery zone | I-003 |
| 3 | System assigns the shopper to the order | — |
| 4 | System reveals the customer's delivery location to the shopper | C-009 |
| 5 | System displays: full item list, customer notes, delivery address, suggested route to market | D-005 |
| 6 | Shopper views the delivery details and begins navigation | — |
| 7 | Shopper travels to the market location | — |
| 8 | Shopper arrives at the market and taps "Arrived at Market" | D-005 |
| 9 | System records arrival timestamp and begins the shopping timer | D-011 |

**Alternative Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 2a | Shopper GPS is > 2 km outside the zone | System rejects the acceptance. Display message: "You are too far from this market to accept this order." Offer moves to next shopper. |
| 7a | Market is closed upon arrival | Shopper marks "Market Closed" in app. System checks alternative markets within 2 km. If available, offers alternative. If not, order cancelled with no penalty. |
| 8a | Shopper takes more than 10 minutes to arrive after acceptance | System flags as "Delayed Arrival" and sends reminder. After 20 minutes without arrival, support is notified. |
| 8b | Navigation is not available in app | System opens default maps app on shopper's device with directions to market. |

**Exception Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 1a | Shopper's device crashes or loses power after accept | Shopper reopens app and order is still assigned. System detects prolonged inactivity and escalates after 15 minutes. |
| 7b | Shopper is involved in an accident / emergency en route | Shopper triggers "Emergency" in app. Order is cancelled with no penalty. Customer notified. |

**Post Conditions:**
- Order status is "Shopping"
- Shopping timer is running
- Shopper is at the market
- Customer is notified that shopping has started

---

### 2.4 Shop Items & Handle Substitutions

| Aspect | Detail |
|--------|--------|
| **Actors** | Shopper, Customer (via chat), Platform System |
| **Trigger** | Shopper taps "Arrived at Market" |
| **Preconditions** | Shopper is physically at the market location. Order is in "Accepted" status. Item list is visible. |

**Main Flow:**

| Step | Description | Business Rules |
|------|-------------|----------------|
| 1 | Shopper views the item list in the app | D-006 |
| 2 | Shopper locates each item in the market | — |
| 3 | For each item found: shopper taps "Found" (item is checked off the list) | D-006 |
| 4 | For each item NOT found: shopper checks the customer's substitution preference | D-007 |
| 5 | **If preference is "Best Match"**: shopper selects a similar item (same type, comparable quality, similar price). Marks as "Substituted" with note. | D-007 |
| 6 | **If preference is "Contact Me"**: shopper sends a chat message with photos of 2-3 alternatives and prices. Waits up to 3 minutes for customer response. | D-007, J-001 |
| 7 | If customer responds: shopper follows their choice. | D-007 |
| 8 | If customer does not respond within 3 minutes: shopper proceeds with best judgment. | D-007 |
| 9 | **If preference is "No Substitutions"**: shopper marks item as "Not Available" | D-007 |
| 10 | Shopper continues until all items are scanned (Found/Substituted/Not Available) | — |

**Alternative Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 3a | Item quantity is different from what customer requested (e.g., bananas priced per bunch, customer wanted per piece) | Shopper uses best judgment for quantity. If significantly different from order, contacts customer via chat. |
| 6a | Customer requests a substitution that is unreasonable (e.g., much more expensive) | Shopper may suggest an alternative or decline via chat. If disputed, support resolves post-order. |
| 10a | Shopping takes more than 60 minutes | System flags "Extended Shopping" alert to customer and shopper. At 90 minutes, support notified. |
| 10b | Vendor refuses to sell to shopper (suspicious of platform shopping) | Shopper contacts support. Support attempts to call vendor. If unsuccessful, shopper may skip that vendor and mark items as Not Available. |

**Post Conditions:**
- All items resolved (Found/Substituted/Not Available)
- Shopper proceeds to checkout
- Customer has visibility into item statuses via the app

---

### 2.5 Checkout, Receipt Upload & Payment

| Aspect | Detail |
|--------|--------|
| **Actors** | Shopper, Platform System |
| **Trigger** | Shopper has finished selecting all items and is ready to pay at the vendor |
| **Preconditions** | All resolvable items have been handled. Shopper is at the checkout counter. |

**Main Flow:**

| Step | Description | Business Rules |
|------|-------------|----------------|
| 1 | Shopper taps "Proceed to Checkout" in app | — |
| 2 | Shopper pays the vendor for the purchased items using their own funds (mobile money or cash) | — |
| 3 | Shopper requests an itemised receipt from the vendor | D-008 |
| 4 | Shopper takes a clear photo of the receipt and uploads it through the app | D-008 |
| 5 | System validates receipt photo (basic check — not blurry, contains numbers) and stores it | — |
| 6 | System displays the final cost breakdown to the shopper: item costs, service fee, delivery fee | — |
| 7 | Shopper confirms the final amounts | — |
| 8 | System sends the final cost to the customer for approval (if variance > 10% from estimate) | F-004 |
| 9 | Shopper marks "Shopping Complete" | — |
| 10 | System transitions order to "In Delivery" status and starts delivery timer | D-004, E-003 |

**Alternative Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 3a | Vendor does not provide itemised receipt (common in informal markets) | Shopper manually enters each item's price in the app. Uploads a photo of the total receipt (non-itemised). |
| 5a | Receipt photo is blurry/unreadable | System prompts shopper to retake. After 3 attempts, allows submission with "Photo Quality Warning" flag for admin review. |
| 8a | Customer does not respond to final cost approval within 5 minutes | Order proceeds at original estimate. The platform covers overshoot < 10%. |
| 9a | Final cost exceeds customer's pre-authorisation hold by > 20% | System requires explicit customer OTP approval before proceeding. |

**Exception Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 4 | Shopper's camera is non-functional | Shopper may request manual price entry with admin verification. Order flagged for review. |
| 8b | Mobile money pre-authorisation insufficient for final amount | System attempts to increase the hold. If customer balance insufficient, switches to partial hold + COD for the difference. |

**Post Conditions:**
- Receipt is uploaded and stored
- Final cost is communicated and approved (or handled per alternatives)
- Order status is "In Delivery"
- Delivery timer has started
- Shopper is now responsible for delivering items to customer

---

### 2.6 Deliver Order to Customer

| Aspect | Detail |
|--------|--------|
| **Actors** | Shopper, Customer, Platform System |
| **Trigger** | Shopper marks "Shopping Complete" and begins travel to delivery location |
| **Preconditions** | Items are packed. Delivery address is known. GPS is active. |

**Main Flow:**

| Step | Description | Business Rules |
|------|-------------|----------------|
| 1 | Shopper begins navigation to the customer's delivery location | — |
| 2 | System shares shopper's live GPS location with customer | C-001 |
| 3 | System displays ETA to the customer (auto-updating based on GPS) | E-003 |
| 4 | Shopper arrives at the delivery location and taps "Arrived" | E-002 |
| 5 | System verifies shopper GPS is within 100m of delivery address | E-002 |
| 6 | Shopper contacts customer via masked call or chat to coordinate handover | J-002 |
| 7 | Shopper hands over the items to the customer | — |
| 8 | Shopper takes a photo of delivered items (with customer or delivery location) | E-002 |
| 9 | Customer confirms receipt in app (or after 15 minutes automatically) | E-002, E-007 |
| 10 | Shopper taps "Confirm Delivery" | — |
| 11 | System marks order as "Delivered" | — |
| 12 | Shopper receives delivery confirmation notification | — |
| 13 | System processes payment: release escrow to shopper minus platform fees | M-010, F-005 |

**Alternative Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 4a | Shopper cannot find the delivery location (incorrect address, hard-to-find location) | Shopper contacts customer via chat for directions. If unreachable after 10 minutes, follows Unavailable Customer procedure (E-004). |
| 5a | GPS shows shopper > 100m from address but shopper is at the correct location (GPS inaccuracy) | Shopper can override the GPS check with a note. Override is logged for audit. |
| 6a | COD payment | Shopper collects cash from customer. Counts and confirms in app. |
| 9a | Customer does not confirm receipt within 15 minutes | System auto-confirms delivery. Ongoing dispute window is still available. |
| 12a | Payment settlement to shopper fails | System queues payment for retry. Notifies shopper that settlement is pending but confirmed. |

**Exception Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 3a | Delivery taking longer than SLA (30 min within 5km, 45 min for 5-10km) | System sends delay notification to customer with updated ETA. Support alerted if > 50% over SLA. |
| 6b | Customer is abusive or aggressive | Shopper may mark "Safety Concern" and leave without completing delivery. Support handles resolution. Shopper compensated. |

**Post Conditions:**
- Order status is "Delivered"
- 15-minute inspection window is active
- Shopper's earnings are credited (pending settlement period)
- Rating prompts are pending
- Shopper is now available for new offers

---

### 2.7 Receive Earnings & Review Rating

| Aspect | Detail |
|--------|--------|
| **Actors** | Shopper, Platform System |
| **Trigger** | Order is marked "Delivered" |
| **Preconditions** | Order has been delivered and confirmed |

**Main Flow:**

| Step | Description | Business Rules |
|------|-------------|----------------|
| 1 | System calculates shopper's earnings: delivery fee + 5% shopping commission | F-005 |
| 2 | Earnings are added to shopper's "Pending Balance" (not yet available for withdrawal) | F-010 |
| 3 | System displays earnings summary on shopper's order completed screen | — |
| 4 | Within 24 hours (48 hours for new shoppers), pending balance moves to "Available Balance" | F-006 |
| 5 | Shopper receives push notification: "Your earnings for Order #1234 are now available" | — |
| 6 | Shopper may withdraw available balance to their mobile money account at any time | F-010 |
| 7 | After order completion, shopper is prompted to rate the customer (blind period applies) | H-002, H-006 |
| 8 | Shopper submits rating | — |
| 9 | After both parties have rated (or 72 hours elapsed), ratings are revealed | H-006 |
| 10 | Shopper can view their updated overall rating in their profile | H-003 |

**Alternative Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 6a | Withdrawal amount exceeds daily limit (200,000 TZS) | System displays the limit and allows the shopper to enter a lower amount or withdraw the maximum. |
| 7a | Shopper skips rating | No penalty. Customer rating is displayed after 72 hours regardless. |
| 9a | Shopper received a low rating (1-2 stars) | System shows the rating with the customer's written feedback (if provided). If the shopper's average drops below 3.5, a quality warning is triggered. |

**Post Conditions:**
- Shopper has received earnings (pending or available)
- Rating has been submitted (or skipped)
- Shopper can go online for the next order
- Shopper's performance metrics are updated

---

## 3. Administrator Workflows

### 3.1 Shopper Vetting & Approval

| Aspect | Detail |
|--------|--------|
| **Actors** | Operations Admin, Platform System, Prospective Shopper |
| **Trigger** | A new shopper application is submitted with status "Pending Verification" |
| **Preconditions** | Applicant has completed registration, uploaded documents, and consented to background check |

**Main Flow:**

| Step | Description | Business Rules |
|------|-------------|----------------|
| 1 | System notifies Operations Admin of new pending application | L-010 |
| 2 | Admin opens the application review screen | — |
| 3 | System displays: applicant info, ID photos, selfie, transport documents, background check result | B-001, B-002, B-003 |
| 4 | Admin manually compares ID photo to selfie for facial match verification | B-002 |
| 5 | Admin reviews transport documents (if applicable): driving licence, registration, insurance validity | B-009 |
| 6 | Admin reviews background check result | B-003 |
| 7 | Admin makes decision: **Approve** or **Reject** | — |
| 8 | If **Approve**: Admin sets initial shopper tier (Standard) and any applicable notes | — |
| 9 | If **Reject**: Admin selects rejection reason and writes internal notes (not shared with applicant) | — |
| 10 | System sends notification to applicant with result | — |
| 11 | If approved: system prompts applicant to complete onboarding training | B-004 |
| 12 | If rejected: system displays generic rejection reason (appeal process included) | B-012 |

**Alternative Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 4a | Facial match is uncertain (different lighting, angle) | Admin may request additional verification (live video call). If still uncertain, reject with option to reapply. |
| 5a | Transport documents are expired but applicant provided renewal receipt | Admin may grant provisional approval (30 days) with note to submit renewed documents. |
| 7a | Borderline case — admin is unsure | Application is escalated to Super Admin for final decision. |
| 11a | Applicant does not complete onboarding within 7 days | Application expires. Applicant must reapply. |

**Post Conditions:**
- Application is approved or rejected
- Approved applicant proceeds to onboarding
- Rejected applicant receives notification with appeal option
- Application record is logged with admin ID and timestamp

---

### 3.2 Dispute Resolution

| Aspect | Detail |
|--------|--------|
| **Actors** | Support Agent, Operations Admin, Customer, Shopper, Platform System |
| **Trigger** | A dispute is escalated to the support queue after triage (L-008) |
| **Preconditions** | Dispute has been submitted by customer or flagged by system. Automated resolution path was not applicable. |

**Main Flow:**

| Step | Description | Business Rules |
|------|-------------|----------------|
| 1 | System assigns dispute ticket to next available Support Agent | L-008 |
| 2 | Agent opens the dispute case with full details: order info, customer claim, evidence, chat logs | K-006 |
| 3 | Agent reviews receipt photo and compares to item list | D-008 |
| 4 | Agent reviews chat history between customer and shopper | I-010 |
| 5 | Agent reviews shopper's GPS history (delivery location verification) | C-001, E-002 |
| 6 | Agent contacts both parties if additional information is needed | — |
| 7 | Agent determines: Shopper Error / Customer Error / Inconclusive / Platform Error | — |
| 8 | Based on determination: | — |
|    | • **Shopper Error**: Agent issues full or partial refund to customer. Shopper's rating adjusted. May trigger shopper quality process (B-007). | G-007 |
|    | • **Customer Error**: Claim rejected. Explanation sent to customer. | G-009 |
|    | • **Inconclusive**: Platform issues 50% goodwill refund. | — |
|    | • **Platform Error**: Full refund. Platform covers costs. | G-006 |
| 9 | Agent writes resolution summary and closes the case | — |
| 10 | System notifies both parties of resolution | — |
| 11 | If shopper error found: system updates shopper's quality metrics | — |

**Alternative Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 2a | High-value dispute (> 100,000 TZS) | Case is escalated directly to Operations Admin, bypassing Support Agent queue. |
| 6a | Customer escalates agent's decision | Case moved to Operations Admin for final review (G-009). |
| 10a | Shopper disputes the finding | Shopper may appeal via B-012. Appeal is reviewed by a different admin than the original agent. |

**Post Conditions:**
- Dispute is resolved with a clear decision
- Refund processed (if applicable)
- Both parties notified
- Case logged for quality analytics
- If shopper error, quality process initiated

---

### 3.3 Fraud Alert Investigation

| Aspect | Detail |
|--------|--------|
| **Actors** | Operations Admin, Platform System, Customer, Shopper |
| **Trigger** | System generates a fraud alert based on detection rules (Section I) |
| **Preconditions** | Alerting rule has been triggered. Alert is logged in the fraud dashboard. |

**Main Flow:**

| Step | Description | Business Rules |
|------|-------------|----------------|
| 1 | System adds alert to fraud dashboard with severity level (Low/Medium/High/Critical) | I-001 through I-011 |
| 2 | Operations Admin reviews the alert queue | — |
| 3 | Admin opens the flagged case and reviews: user history, order pattern, GPS data, device info, payment records | — |
| 4 | Admin determines: **False Alarm** (dismiss) or **Suspicious** (investigate further) or **Confirmed Fraud** (take action) | — |
| 5 | If **False Alarm**: Admin dismisses with note. System adjusts detection threshold if applicable. | — |
| 6 | If **Suspicious**: Admin places user account under observation (monitoring mode — flagged for increased scrutiny but not blocked). Duration: 7 days default. | — |
| 7 | If **Confirmed Fraud**: Admin takes appropriate action: | — |
|    | • Customer fraud (fake orders, chargeback abuse): Account suspended. | A-005 |
|    | • Shopper fraud (theft, GPS spoofing, price inflation): Account permanently deactivated. | B-007 |
|    | • Payment fraud (stolen mobile money): Account frozen. Incident reported to relevant authorities. | — |
| 8 | Admin writes case notes and closes investigation | — |
| 9 | System logs all actions with admin ID and timestamp | L-002 |

**Alternative Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 2a | Alert is "Critical" severity (active financial loss in progress) | Admin receives immediate push notification. Case is auto-assigned for immediate review. Payment processing for the involved accounts may be temporarily paused. |
| 7a | Legal reporting required (money laundering indicators) | Admin initiates regulatory reporting workflow. Platform legal counsel notified. |

**Post Conditions:**
- Alert is resolved (dismissed, monitoring, or action taken)
- User account status updated (if applicable)
- Case is logged for pattern analysis
- Detection rules may be adjusted based on findings

---

### 3.4 Emergency Order Intervention

| Aspect | Detail |
|--------|--------|
| **Actors** | Operations Admin, Customer, Shopper, Platform System |
| **Trigger** | An order is flagged with an emergency event: shopper emergency (K-001), order abandonment (G-010), critical SLA breach, or natural disaster (K-005) |
| **Preconditions** | Emergency event has been detected by system or reported by user |

**Main Flow:**

| Step | Description | Business Rules |
|------|-------------|----------------|
| 1 | System notifies Operations Admin of the emergency with details | — |
| 2 | Admin opens the order in the admin dashboard | — |
| 3 | Admin reviews current order status, timeline, and parties involved | — |
| 4 | Admin assesses the situation: | — |
|    | • **Shopper emergency**: Confirm shopper safety. Cancel order with no penalty. Pay shopper for time invested. Contact customer. | K-001 |
|    | • **Order abandonment**: Attempt to reassign. If unable within 15 minutes, cancel with full refund to customer. Compensate abandoned shopper's time. | G-010 |
|    | • **SLA critical breach**: Contact shopper and customer. Coordinate resolution. Extend ETA if needed. | — |
|    | • **Zone emergency (disaster/curfew)**: Suspend zone operations. Cancel all active orders in zone with full refunds. | K-005 |
| 5 | Admin executes the appropriate action in the system | — |
| 6 | Admin contacts affected parties (via app notification, SMS, or call) | — |
| 7 | Admin documents the incident for post-event review | — |

**Alternative Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 4a | Situation is unclear (conflicting information) | Admin attempts to contact both parties directly. If unreachable, admin may place the order on hold (paused, not cancelled) pending clarification. |
| 6a | Customer is dissatisfied with emergency resolution | Admin may offer additional compensation (platform credit, discount on next order) at discretion up to 20,000 TZS without escalation. |

**Post Conditions:**
- Emergency is resolved with clear action taken
- Affected parties are informed
- Incident is documented for review
- Financial adjustments are processed (refunds, compensation)

---

### 3.5 Platform Monitoring & Reporting

| Aspect | Detail |
|--------|--------|
| **Actors** | Operations Admin, Super Admin, Platform System |
| **Trigger** | Admin accesses the monitoring dashboard (scheduled check or prompted by alert) |
| **Preconditions** | Admin is logged in with appropriate access level |

**Main Flow:**

| Step | Description | Business Rules |
|------|-------------|----------------|
| 1 | Admin opens the monitoring dashboard | L-010 |
| 2 | System displays live metrics: active orders map, shopper availability, assignment times | L-010 |
| 3 | Admin reviews daily/weekly KPIs: GMV, order volume, fulfillment rate, acceptance rate, dispute rate | L-005 |
| 4 | Admin reviews system health: API response times, payment provider status, server load | — |
| 5 | If metrics are within acceptable ranges: admin may proceed to reporting | — |
| 6 | If metrics breach thresholds: admin investigates root cause and initiates corrective actions | — |
| 7 | Admin generates scheduled reports (daily ops summary, weekly performance, monthly financial) | L-004 |
| 8 | Admin reviews outstanding support tickets and dispute queue | — |
| 9 | Admin reviews pending shopper applications | — |
| 10 | Admin logs out or continues monitoring | — |

**Alternative Flows:**

| Step | Condition | Action |
|------|-----------|--------|
| 6a | Payment provider API is down | Admin initiates emergency procedure: switch to backup provider if available, or activate manual payment processing mode. Trigger incident report. |
| 6b | Shopper supply in a zone has dropped below critical threshold | Admin may activate zone-level interventions: expand radius, increase fees, send recruitment push notifications to nearby shoppers. |

**Post Conditions:**
- Platform status is assessed
- Reports are generated and distributed (if scheduled)
- Any metric breaches are addressed
- Operational decisions are documented

---

## 4. Future Vendor Workflow (V2 Mock-up)

> **Note:** This is a preliminary workflow for V2 planning purposes. It will be fully specified during V2 business analysis.

### 4.1 Vendor Registration & Inventory Setup

| Aspect | Detail |
|--------|--------|
| **Actors** | Vendor (shop owner), Platform System, Operations Admin |
| **Trigger** | Vendor expresses interest in joining the platform via vendor portal or sales outreach |
| **Preconditions** | V2 vendor module is active. Vendor has a physical shop location with sellable inventory. |

**Draft Main Flow:**

| Step | Description |
|------|-------------|
| 1 | Vendor submits application via vendor portal: business name, location, type, owner details |
| 2 | Platform verifies vendor business registration and physical location |
| 3 | Vendor signs integration agreement |
| 4 | Vendor onboards their inventory into the system (via API, CSV upload, or manual entry) |
| 5 | Platform verifies sample inventory accuracy (spot-check 10 items for price/availability) |
| 6 | Vendor sets operating hours, delivery radius, and commission preferences |
| 7 | Vendor account is activated |
| 8 | Vendor inventory becomes visible to customers in the app (listed under "Shop at [Vendor Name]") |

**Business Rules:** M-004 (Vendor Integration Program)

---

## 5. Future Corporate Client Workflow (V2 Mock-up)

> **Note:** This is a preliminary workflow for V2 planning purposes.

### 5.1 Corporate Account Onboarding & Bulk Ordering

| Aspect | Detail |
|--------|--------|
| **Actors** | Corporate Client (admin), Platform Operations, Shoppers |
| **Trigger** | Business submits corporate account application |
| **Preconditions** | V2 corporate module is active. Business has valid registration. |

**Draft Main Flow:**

| Step | Description |
|------|-------------|
| 1 | Business submits corporate application: company name, registration number, tax ID, admin contact |
| 2 | Platform verifies company registration and conducts credit check |
| 3 | Corporate agreement signed with NET-30 invoicing terms |
| 4 | Corporate account created with designated admin user(s), budget limits, and approval workflows |
| 5 | Corporate admin sets up employee groups with ordering privileges and spending limits |
| 6 | Employees within the organisation can place orders charged to the corporate account |
| 7 | Orders are fulfilled via normal customer workflow |
| 8 | Corporate admin receives monthly consolidated invoice with order-level breakdown |
| 9 | Payment due within 30 days via bank transfer or mobile money |

**Business Rules:** M-003 (Corporate Account — Bulk Orders)

---

## Workflow Cross-Reference Matrix

| Workflow | Primary Business Rules | Actors |
|----------|----------------------|--------|
| 1.1 Customer Registration | A-001, A-003, A-007, I-001, L-007 | Customer, System |
| 1.2 Submit Order | D-001, D-002, D-003, D-014, F-001, F-002, F-003, F-007, F-012, I-007 | Customer, System |
| 1.3 Track Order | C-001, C-003, C-004, D-004, D-006, J-001, J-002, J-004 | Customer, Shopper, System |
| 1.4 Receive Delivery | E-002, E-007, F-004, F-012, H-001, H-002, M-010 | Customer, Shopper, System |
| 1.5 Cancel Order | G-001, G-002, G-003, G-008 | Customer, System, Shopper |
| 1.6 Request Refund | E-007, G-007, G-008, G-009, K-006, K-008, L-008 | Customer, System, Support Agent |
| 2.1 Shopper Registration | B-001, B-002, B-003, B-004, B-009, L-007 | Shopper, System, Admin |
| 2.2 Go Online & Receive Offers | B-005, B-011, C-001, C-002, C-003, J-003 | Shopper, System |
| 2.3 Accept & Navigate | C-009, D-005, D-011, I-003, K-001, K-003 | Shopper, System |
| 2.4 Shop Items | D-006, D-007, D-011, J-001, J-005 | Shopper, Customer, System |
| 2.5 Checkout & Receipt | D-008, D-011, F-004, F-011 | Shopper, System |
| 2.6 Deliver Order | E-001, E-002, E-003, E-004, E-005, J-002, M-010 | Shopper, Customer, System |
| 2.7 Receive Earnings | F-005, F-006, F-010, H-002, H-003, H-006 | Shopper, System |
| 3.1 Shopper Vetting | B-001, B-002, B-003, B-009, B-012, L-001 | Admin, System, Shopper |
| 3.2 Dispute Resolution | G-006, G-007, G-009, K-006, L-001, L-008 | Support Agent, Admin, System |
| 3.3 Fraud Investigation | I-001 through I-011, A-005, B-007, L-002 | Admin, System |
| 3.4 Emergency Intervention | G-010, K-001, K-005 | Admin, System |
| 3.5 Monitoring | L-004, L-005, L-010 | Admin, System |

---

*This document feeds into Release Scope Definition (`05-release-scope.md`) and the Master SRS Prompt (`06-master-srs-prompt.md`).*
