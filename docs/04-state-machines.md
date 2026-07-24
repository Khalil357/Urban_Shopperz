# Urban Shopper Platform — State Machines

> **Document Type:** Business Analysis  
> **Status:** Complete  
> **Date:** 2026-07-24  
> **Version:** 1.0  
> **Cross-Reference:** Business Rules — `03-business-rules.md`, Domain Model — `02-domain-model.md`  
> **Phase:** 4 of 12

---

## Table of Contents

1. [1. Order State Machine](#1-order-state-machine)
2. [2. Shopper State Machine](#2-shopper-state-machine)
3. [3. Payment State Machine](#3-payment-state-machine)
4. [4. Dispute State Machine](#4-dispute-state-machine)

---

## 1. Order State Machine

The **Order State Machine** is the most complex and important state machine on the platform. It governs the lifecycle of every customer request from creation through archival. Invalid transitions are rejected by the system.

### State Diagram

```
                                    ┌──────────────────────────────────────────────────────────────┐
                                    │                      Cancelled                               │
                                    │  (from Created, Offered, Accepted, Travelling, Shopping)     │
                                    └──────────────────────────────────────────────────────────────┘
                                        ▲            ▲           ▲           ▲           ▲
                                        │            │           │           │           │
                                        │            │           │           │           │
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│ Created  │──>│Awaiting │──>│ Queued   │──>│ Offered  │──>│ Accepted │──>│Travelling│──>│ Shopping │
│          │   │Payment  │   │For       │   │          │   │          │   │To Market │   │          │
└──────────┘   │Verific. │   │Assignment│   └──────────┘   └──────────┘   └──────────┘   └──────────┘
               └──────────┘   └──────────┘                                                     │
                                                                                                 │
                                                                                                 v
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐         │
│ Archived │<──│Completed │<──│Delivered │<──│In Delivery│<──│ Receipt  │<──│ Shopping │<────────┘
│          │   │          │   │          │   │          │   │ Verified │   │ Complete │
└──────────┘   └──────────┘   └──────────┘   └──────────┘   └──────────┘   └──────────┘
                                                                                  │
                                                                                  │
                                                                                  v
                                                                           ┌──────────┐
                                                                           │ Cancelled│
                                                                           │ (Special)│
                                                                           └──────────┘
```

### State Transition Table

| From | To | Trigger Event | Guard Condition | Actions | Business Rules |
|------|----|---------------|-----------------|---------|----------------|
| Created | Awaiting Payment Verification | OrderSubmitted | All required fields present (D-001). Customer account valid. | Place pre-auth hold on customer wallet. Start payment verification timer. | D-001, D-003, F-003 |
| Awaiting Payment Verification | Queued for Assignment | PaymentVerified | Pre-auth hold successful (F-003). | Generate order ID. Queue for assignment engine. Log transition. | D-004, F-003 |
| Awaiting Payment Verification | Cancelled | PaymentFailed | Pre-auth hold failed after retries (F-011). | Notify customer payment failed. Release any partial holds. Log transition. | F-011 |
| Queued for Assignment | Offered | OrderReadyForAssignment | Eligible shoppers exist in zone. Zone is active. | Calculate Assignment Scores (C-011). Send offer to highest-scoring shopper (J-003). Start 30-second timer. | C-002, C-004, C-011, J-003 |
| Offered | Accepted | ShopperAcceptedOffer | Shopper accepted within 30 seconds. Shopper GPS is within zone (I-003). | Assign shopper to order. Reveal customer delivery location (C-009). Send route to market. Start 10-min dynamic ETA timer (D-005). Notify customer (J-004). | C-003, C-009, D-005, I-003 |
| Offered | Offered | ShopperDeclinedOffer | Shopper declined or 30s elapsed. Next shopper exists in queue. | Offer to next highest-scoring shopper. New 30-second timer. | C-003, C-004 |
| Offered | Cancelled | CascadeTimeout | 3 minutes elapsed (C-004). No shopper accepted. | Notify customer with options (D-010). Release pre-auth hold. Log cancellation reason. | C-004, D-010, G-005 |
| Accepted | Travelling to Market | ShopperEnRoute | GPS shows shopper moving toward market. | Calculate dynamic Expected Arrival Time (D-005). Start grace period timer. Update customer notification. | D-005, C-001 |
| Travelling to Market | Shopping | ShopperArrivedAtMarket | Shopper taps "Arrived" OR GPS confirms arrival within ETA + grace period. | Start shopping timer (D-011 tiered). Notify customer shopping started. | D-005, D-011 |
| Travelling to Market | Cancelled | MarketClosed | Shopper reports market closed (K-003). No alternative market available. | Cancel with no penalty. Notify customer. Release pre-auth hold. | K-003, G-004 |
| Travelling to Market | Cancelled | ShopperCancelled | Shopper cancels for valid reason per G-004. | Review cancellation reason. Apply G-004 penalty rules if unauthorised. | G-004 |
| Shopping | Shopping | ItemStatusUpdated | Shopper marks item Found/Substituted/Not Available. | Update item status in real-time (D-006). If substituted, handle per D-007 (Contact Me/Best Match/No Subs). | D-006, D-007 |
| Shopping | Shopping Complete | AllItemsResolved | All items marked Found, Substituted, or Not Available. | Prompt shopper to upload receipt. | D-006 |
| Shopping Complete | Receipt Verified | ReceiptUploadedAndVerified | Receipt photo(s) uploaded (D-008). Price verification passed (I-004) or flagged. | Calculate final amounts (F-004). If variance > 10% OR > 10,000 TZS, request customer approval. | D-008, F-004, I-004 |
| Shopping Complete | Cancelled | CustomerCancelsAfterShopping | Customer cancels after shopping started (G-003). | Charge cancellation fee: full delivery fee + 10% restocking fee. Shopper gets full delivery fee + full shopping fee. Customer may choose to receive already-purchased items. | G-003 |
| Receipt Verified | In Delivery | DeliveryStarted | Shopper marks "In Delivery" OR GPS indicates departure from market. | Start dynamic delivery timer (E-003). Share ETA with customer (J-004). Begin GPS route tracking. | E-003, J-004 |
| In Delivery | In Delivery | ETAUpdated | ETA changes by > 5 minutes during delivery. | Recalculate ETA. Notify customer of updated ETA with reason (E-009). Notify shopper if rerouting recommended. | E-009 |
| In Delivery | Delivered | ShopperArrivedAndDelivered | Shopper confirms arrival (GPS verified within 100m). Photo taken (E-002). Customer or Authorized Recipient received items. | Record delivery timestamp. Start inspection window (E-007 — category-based). Send delivery confirmation to customer. | E-002, E-007 |
| In Delivery | Delivered | UnavailableCustomer | Customer not available after 5-min contact attempt. Items left at safe location OR returned (E-004). | If safe drop: photo proof, mark delivered. If returned: cancel order, initiate redelivery option (E-008) or cancellation refund (G-003). | E-004, E-008 |
| Delivered | Completed | InspectionWindowElapsed | Inspection window expired with no issues reported (E-007 category-dependent). | Auto-complete order. Release delivery confirmation process final payment (F-004). Prompt ratings (H-001, H-002). | E-007, H-001, H-002 |
| Delivered | Completed | CustomerConfirmed | Customer confirmed items received within inspection window. Issues reported go to Dispute (G-011). | Complete order. If issues reported, enter dispute workflow. If no issues, process final payment. | E-007, H-001 |
| Completed | Archived | ArchiveTimerElapsed | 90 days since "Completed" status. No open disputes on order (D-013). | Move order data to cold storage. Retain per retention policy (1 year min). | D-013, L-003 |
| Any* | Cancelled | PlatformInitiatedCancellation | Fraud detected (I-001—I-011), legal requirement, force majeure, or cascade timeout (G-005). | Cancel with no penalty to customer or shopper. Release all holds. Full refund if applicable. Notify both parties. | G-005 |
| Any* | Cancelled | CustomerCancellation | Customer initiates cancellation (varies by state — G-001, G-002, G-003). | Calculate cancellation fee per state. Compensate shopper per G-002/G-003. Process refund minus fees. | G-001, G-002, G-003 |

**Note:** *Customer cancellation is only permitted from Created, Offered, Accepted, Travelling, and Shopping states. Platform-initiated cancellation is permitted from any state.*

### Invalid Transitions (Explicitly Rejected)

| From | To | Why Invalid |
|------|----|-------------|
| Created | Shopping | Payment not yet verified, shopper not yet assigned |
| Offered | Delivered | No intermediate states (shopping, receipt not completed) |
| Accepted | Receipt Verified | Shopping not yet completed |
| Shopping | Completed | Delivery not yet performed |
| Completed | Created | Cannot reprocess a completed order |
| Cancelled | Any | Terminal state — once cancelled, order cannot resume |
| Archived | Any | Terminal state — order data is in cold storage |

---

## 2. Shopper State Machine

The **Shopper State Machine** governs the shopper's availability and work status on the platform.

### State Diagram

```
                ┌──────────────┐
     ┌─────────│   Offline    │◄──────────┐
     │         └──────────────┘           │
     │          ▲           ▲             │
     │          │           │             │
     │          │           │             │
     │    ┌─────┴─────┐ ┌───┴──────┐     │
     │    │  Online   │ │  Online  │     │
     │    │ (Waiting) │ │(Offered) │     │
     │    └─────┬─────┘ └────┬─────┘     │
     │          │            │           │
     │          ▼            ▼           │
     │    ┌──────────────────────────┐   │
     │    │       Assigned           │   │
     │    │  (Travelling to Market)  │   │
     │    └────────────┬─────────────┘   │
     │                 │                 │
     │                 ▼                 │
     │    ┌──────────────────────────┐   │
     │    │       Shopping           │   │
     │    └────────────┬─────────────┘   │
     │                 │                 │
     │                 ▼                 │
     │    ┌──────────────────────────┐   │
     │    │      Delivering          │   │
     │    └────────────┬─────────────┘   │
     │                 │                 │
     │                 ▼                 │
     │    ┌──────────────────────────┐   │
     │    │   Order Complete         │   │
     │    │   (Returns to Online)    ├───┘
     │    └──────────────────────────┘
     │
     └─────────────────────────────────────┘
              (App closed, no heartbeat)
```

### State Transition Table

| From | To | Trigger Event | Guard Condition | Actions | Business Rules |
|------|----|---------------|-----------------|---------|----------------|
| Offline | Online (Waiting) | ShopperGoesOnline | Shopper taps "Go Online". App connected. GPS available. | Set status to Active. Start GPS ping at 30-second frequency (C-001). Shopper becomes eligible for offers. | B-005, C-001 |
| Online (Waiting) | Online (Offered) | OfferReceived | Assignment engine selects shopper (C-011). | Receive order offer. Display countdown timer. Start GPS ping at 10-second frequency. | C-003, C-011 |
| Online (Offered) | Online (Waiting) | OfferDeclinedByShopper | Shopper taps Decline. | Return to waiting state. Update acceptance rate (B-011). Resume 30-second GPS ping. | B-011 |
| Online (Offered) | Online (Waiting) | OfferTimedOut | 30 seconds elapsed with no action. | Treat as decline per C-003. Update acceptance rate. Resume 30-second GPS ping. | C-003, B-011 |
| Online (Offered) | Assigned | OfferAccepted | Shopper taps Accept within 30 seconds. GPS validated in zone (I-003). | Transition to order fulfillment. Start GPS ping at 5-second frequency. Calculate dynamic ETA to market (D-005). | C-003, D-005, I-003 |
| Assigned (Travelling) | Shopping | ShopperArrivedAtMarket | GPS confirms arrival OR shopper taps "Arrived". | Start shopping timer (D-011 tiered). Change GPS ping to 15-second frequency. Update order status. | D-005, D-011, C-001 |
| Shopping | Shopping (ItemStatus) | ItemMarked | Shopper marks item Found/Substituted/Not Available. | Update item visibility for customer. If Contact Me substitution, send notification. | D-006, D-007 |
| Shopping | Delivering | ShoppingComplete | All items resolved. Receipt uploaded. | Calculate final amounts. Start delivery timer (E-003). Start 5-second GPS ping. | D-008, E-003, C-001 |
| Delivering | Order Complete | DeliveryConfirmed | Items delivered. Customer confirmed OR inspection window elapsed. | Process payment settlement (F-004). Prompt ratings (H-001, H-002). Shopper returns to Online (Waiting). | E-002, F-004, H-001, H-002 |
| Any | Offline | ShopperGoesOffline | Shopper taps "Go Offline". OR App disconnected for 2+ minutes with no heartbeat (B-005). | Set status to Inactive. Stop GPS transmission. Shopper no longer eligible for offers. Current active order is NOT affected. | B-005 |
| Any | Offline | EmergencyTriggered | Shopper triggers emergency (K-001). | Cancel current order with no penalty (K-001). Notify support. Set shopper to Offline. | K-001 |
| Any | Suspended | SuspensionTriggered | Threshold breached per B-007 (acceptance < 40%, completion < 90%, rating < 4.0, fraud, abuse). | Apply B-007 progressive discipline. Notify shopper. Set status to Suspended. | B-007 |
| Suspended | Offline | SuspensionLifted | Appeal approved (B-012) OR retraining completed. OR suspension period expired. | Clear suspension flag. Shopper must manually Go Online. | B-007, B-012 |

### Shopper Lifecycle States (Non-Assignment)

These are account-level states independent of the Online/Offline/Working cycle:

| State | Description | Business Rules |
|-------|-------------|----------------|
| **Pending Verification** | Registered but not yet approved | B-001, B-002, B-003 |
| **Onboarding** | Approved but training not completed | B-004 |
| **Active** | Can go Online/Offline | B-005 |
| **Suspended** | Temporarily banned due to performance or conduct | B-007 |
| **Low Priority** | Assignment Score penalised due to low activity (future, B-006) | B-006 |
| **Deactivated** | Permanently removed from the platform | B-007, G-004 |

---

## 3. Payment State Machine

The **Payment State Machine** governs the financial lifecycle of a single order. It is tightly coupled to the Order State Machine but has its own distinct states because financial events may occur asynchronously.

### State Diagram

```
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│  Pending │──>│ Auth-    │──>│ Captured │──>│ Settled  │──>│ Completed│
│          │   │ orized   │   │          │   │          │   │          │
└──────────┘   └──────────┘   └──────────┘   └──────────┘   └──────────┘
     │               │                              ▲
     │               │                              │
     │               v                              │
     │          ┌──────────┐   ┌──────────┐         │
     │          │ Released │──>│ Cancelled│─────────┘
     │          └──────────┘   └──────────┘
     │
     v
┌──────────┐
│ Cancelled│
│ (no hold)│
└──────────┘

     ┌──────────┐   ┌──────────┐
     │ Captured │──>│  Partial │
     └──────────┘   │  Refund  │
                    └──────────┘
                         │
                         v
                    ┌──────────┐
                    │ Refunded │
                    └──────────┘
```

### State Transition Table

| From | To | Trigger Event | Guard Condition | Actions | Business Rules |
|------|----|---------------|-----------------|---------|----------------|
| Pending | Authorized | PreAuthHoldPlaced | Customer has sufficient mobile money balance. M-Pesa/Mixx API returns success. | Deduct hold amount from customer's wallet/account. Store hold reference. | F-003 |
| Pending | Cancelled | OrderCancelledPreAuth | Customer cancels before any shopper accepts (G-001). | No financial impact. Order never created in payment system. | G-001 |
| Authorized | Released | OrderCancelledPreAcceptance | Customer cancels before shopper acceptance (G-001). | Release pre-auth hold (no charge). Notify customer. | G-001 |
| Authorized | Released | CascadeTimeout | 3-minute cascade completes with no acceptance (C-004). | Release pre-auth hold. Notify customer with options. | C-004, D-010 |
| Authorized | Captured | OrderDelivered | Delivery confirmed. Final amount calculated. Customer approved variance (if > 10% OR > 10,000 TZS). | Charge final amount to customer. Calculate breakdown: item cost, service fee (tiered), delivery fee. | F-004, F-001, F-002 |
| Captured | Refunded | PartialRefundIssued | Item issue dispute resolved per G-007 proportional tiers. | Refund affected item value + proportional delivery adjustment. Log transaction. | G-007, G-011 |
| Captured | Refunded | FullRefundIssued | Platform fault (G-006) OR major issue (> 50% order affected) OR customer cancels after shopping (G-003). | Refund total customer charge. Trigger root cause logging for platform faults (G-006). | G-006, G-003, G-007 |
| Captured | Settled | SettlementTriggered | 48 hours elapsed (or 24h for high-tier shoppers). Order "Completed". No open disputes. | Calculate shopper payout: delivery fee + tiered shopping fee (F-005). Transfer to Shopper Wallet available balance. Deduct platform service fee revenue. | F-005, F-006 |
| Settled | Refunded | PostSettlementRefund | Dispute resolved in customer's favour after settlement already occurred (G-009). | Deduct refund amount from platform (shopper has already been paid). Refund customer. | G-008, G-009 |
| Settled | Completed | SettlementConfirmed | Funds transferred successfully to Shopper Wallet. No further action expected. | Mark payment as completed. Ready for archival per L-003 retention policy. | F-006 |
| Refunded | Completed | RefundSettled | Refund successfully transferred to customer's payment method. | Log final payment status. Ready for audit. | G-008 |

### Payment State Coupling to Order State

| Order State | Payment State | Notes |
|-------------|---------------|-------|
| Created | Pending | Pre-auth not yet attempted |
| Awaiting Payment Verification | Pending → Authorized | Pre-auth is attempted |
| Queued for Assignment → Offered | Authorized | Hold is active |
| Accepted → Travelling → Shopping | Authorized | Hold remains active |
| Shopping Complete → Receipt Verified | Authorized | Final amount calculated |
| In Delivery | Authorized | Waiting for delivery confirmation |
| Delivered | Authorized → Captured | Final charge triggered |
| Completed | Captured → Settled → Completed | 48-hour settlement timer starts on Completed |
| Cancelled (before acceptance) | Authorized → Released → Cancelled | No charge |
| Cancelled (after acceptance) | Authorized → Released → Cancelled | Shopper compensated per G-002/G-003, customer charged cancellation fee |
| Cancelled (platform) | Authorized → Released → Cancelled | Full release, no charge |

---

## 4. Dispute State Machine

The **Dispute State Machine** governs the lifecycle of all dispute types, implementing the unified Dispute Resolution Framework (G-011).

### State Diagram

```
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│ Reported │──>│  Under   │──>│ Evidence │──>│ Decision │──>│ Resolved │──>│  Closed  │
│          │   │  Review  │   │Collection│   │          │   │          │   │          │
└──────────┘   └──────────┘   └──────────┘   └──────────┘   └──────────┘   └──────────┘
     │               │                                              │
     │               │                                              │
     │               v                                              │
     │          ┌──────────┐                                        │
     │          │Automated │────────────────────────────────────────┘
     │          │Validation│  (Clear evidence, < 10,000 TZS)
     │          └──────────┘
     │
     │               ┌──────────┐
     │               │Escalated │  (High-value > 100k TZS, behavioural)
     └──────────────>│  to Ops  │
                     │  Admin   │
                     └──────────┘
```

### State Transition Table

| From | To | Trigger Event | Guard Condition | Actions | Business Rules |
|------|----|---------------|-----------------|---------|----------------|
| Reported | Under Review | DisputeSubmitted | Customer, Shopper, or System initiates dispute. Within applicable reporting window (E-007, K-006). | Assign dispute ID. Log initial report. Gather automatically available evidence (order data, chat logs). | G-011 |
| Reported | Automated Validation | StraightforwardCase | Claim < 10,000 TZS. Clear evidence provided (photo, receipt). Low complexity. | Apply automated resolution per policy (G-007 tiers). Execute refund if applicable. Log decision. | G-011, L-008 |
| Reported | Escalated to Ops Admin | HighValueOrComplex | Claim > 100,000 TZS. OR behavioural complaint (K-007). OR contradictory evidence. | Assign to Operations Admin directly. Bypass standard queue. | L-008, K-007 |
| Under Review | Automated Validation | QualifiedForAuto | Evidence is clear and complete. Case fits automated criteria. | Execute automated resolution. Skip manual queue. | L-008 |
| Under Review | Evidence Collection | EvidenceInsufficient | Existing evidence is insufficient for decision. | Request additional evidence from relevant party (photos, clarification). Set response deadline (24 hours). | G-011 |
| Under Review | Escalated to Ops Admin | ComplexityDiscovered | During review, case proves more complex than initially assessed. | Escalate to Operations Admin. Transfer case notes. | L-008 |
| Under Review | Escalated to Ops Admin | PartyRequestedEscalation | Customer or Shopper requests human escalation (G-009). | Escalate to Operations Admin regardless of automated outcome. | G-009, L-008 |
| Evidence Collection | Decision | EvidenceReceived | Additional evidence submitted within deadline. Evidence is sufficient. | Proceed to decision-making. | G-011 |
| Evidence Collection | Resolved | NoEvidenceProvided | Deadline elapsed with no additional evidence. OR evidence still insufficient. | Default to "insufficient evidence" resolution. Notify parties. If customer-initiated, inform of appeal rights (G-009). | G-011 |
| Automated Validation | Resolved | AutoDecisionMade | Refund processed OR claim denied per policy. | Notify both parties of outcome. Execute financial adjustment (refund or release). | G-007, G-008 |
| Automated Validation | Escalated to Ops Admin | PartyRequestedReview | Customer appeals automated decision within 24 hours (G-009). | Escalate for manual review. Assign to support agent. | G-009 |
| Decision | Resolved | ManualDecisionMade | Support agent or Ops Admin makes final decision. | Notify both parties of decision with rationale. Execute financial adjustments (refund, compensation, payout adjustment). If goodwill applicable, apply per G-009 discretionary policy. | G-009, G-011 |
| Resolved | Closed | ResolutionExecuted | Financial adjustment completed (if any). Both parties notified. No further escalation pending. | Log resolution for analytics. If shopper error found, update shopper quality metrics. If recurring platform fault, trigger engineering review (G-006). | G-011, L-002 |
| Closed | *Resolved* | ExceptionallyReopened | New compelling evidence discovered. Ops Admin approves reopening. | Reopen case. Return to Evidence Collection. Document reason for reopening. | G-011 |

### Dispute Type Routing Rules

| Dispute Type | Initial Routing | SLA Target |
|--------------|----------------|------------|
| Item Discrepancy (Minor, < 10,000 TZS, clear evidence) | Automated Validation | < 1 hour |
| Item Discrepancy (Moderate/Major) | Manual Review (Support Agent) | < 24 hours |
| Shopper Behaviour (Harassment, abuse) | Escalated to Ops Admin (immediate suspension) | < 48 hours |
| Payment Failure | Automated retry + Manual if persists | < 2 hours |
| Cancellation Fee Dispute | Manual Review (Support Agent) | < 24 hours |
| High Value (> 100,000 TZS) | Escalated to Ops Admin | < 24 hours |
| Platform Error | Automated + Engineering review triggered | < 2 hours |

### State Machine Validation Rules

| # | Rule |
|---|------|
| SM-01 | An Order cannot transition from "Delivered" back to "Shopping" — no reverse flow |
| SM-02 | Payment cannot be "Settled" before "Captured" |
| SM-03 | A Dispute cannot be "Closed" without a "Decision" |
| SM-04 | A Shopper cannot receive a new offer while in "Assigned", "Shopping", or "Delivering" state (B-010) |
| SM-05 | "Cancelled" is a terminal state for Orders — no resumption permitted |
| SM-06 | "Archived" is a terminal state for Orders — data moved to cold storage (D-013) |
| SM-07 | "Deactivated" is a terminal state for Shoppers — no reactivation without admin intervention |
| SM-08 | Payment "Refunded" can occur from "Captured" or "Settled" — depending on timing of dispute resolution |
| SM-09 | A Dispute "Decision" must always include a written rationale for audit purposes (L-002) |
| SM-10 | State transitions are logged with: timestamp, from-state, to-state, trigger event, actor, and reason |

---

*This document is Phase 4 of the Urban Shopper Platform specification. It feeds into Phase 5 (BPMN Process Models), Phase 7 (System Architecture), and Phase 8 (IEEE 29148 SRS).*
