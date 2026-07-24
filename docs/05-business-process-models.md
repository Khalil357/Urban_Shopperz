# Urban Shopper Platform — Business Process Model (BPMN)

> **Document Type:** Business Analysis  
> **Status:** Complete  
> **Date:** 2026-07-24  
> **Version:** 1.0  
> **Cross-Reference:** Business Rules — `03-business-rules.md`, State Machines — `04-state-machines.md`, Domain Model — `02-domain-model.md`  
> **Phase:** 5 of 12

---

## Table of Contents

1. [Notation Guide](#1-notation-guide)
2. [Process 1 — Customer Ordering](#2-process-1--customer-ordering)
3. [Process 2 — Shopper Acceptance & Assignment](#3-process-2--shopper-acceptance--assignment)
4. [Process 3 — Shopping Workflow](#4-process-3--shopping-workflow)
5. [Process 4 — Delivery & Fulfillment](#5-process-4--delivery--fulfillment)
6. [Process 5 — Refund Processing](#6-process-5--refund-processing)
7. [Process 6 — Dispute Resolution](#7-process-6--dispute-resolution)

---

## 1. Notation Guide

These process descriptions use the following BPMN notation conventions:

**Symbols used:**
```
[Activity Name]          — Task / Activity
{Decision?}              — Exclusive Gateway (XOR)
⭕                       — Start Event
⏹️                       — End Event
⚡                       — Intermediate Event (timer, message)
---→                     — Sequence Flow
---→[DMZ]                — Flow to another process
Swimlane: [Actor]        — Who performs the activities
```

Each process includes:
- **Swimlanes** — actors involved
- **Sequence flows** — numbered steps
- **Gateways** — decision points
- **Business Rules** — cross-referenced to `03-business-rules.md`
- **State Machine** — cross-referenced to `04-state-machines.md`

---

## 2. Process 1 — Customer Ordering

### Scope
From customer opening the app through order submission and payment pre-authorisation.

### Swimlanes
- **Customer** — initiates and manages the order
- **Platform System** — validates, estimates, and processes
- **Mobile Money Provider** — external payment system

### Process Diagram (Text)

```
Swimlane: Customer
⭕ Start — Customer opens app
  |
  [Select delivery location via pin-drop]
  |
  [Enter items: name, quantity, optional brand/unit/max price]
  |
  [Select shopping preference: Cheapest / Best Quality / Balanced]
  |
  [Set substitution preference per item]
  |
  [Select delivery: ASAP or Scheduled]
  |
  [Review price breakdown: item estimate + service fee + delivery fee]
  |
  {Confirm order?}
  ├── Yes → [Tap "Submit Order"]
  │         |
  │         ↓
  │    Swimlane: Platform System
  │         [Validate order requirements (D-001)]
  │         |
  │         {Validation passes?}
  │         ├── Yes → [Attempt pre-auth hold on mobile money (F-003)]
  │         │         |
  │         │         {Pre-auth successful?}
  │         │         ├── Yes → [Create Order (status: Awaiting Payment Verif.)]
  │         │         │         |
  │         │         │         [Queue for assignment]
  │         │         │         |
  │         │         │         → [DMZ: Process 2 — Shopper Acceptance]
  │         │         │         |
  │         │         │         ⏹️ End — Order created, awaiting shopper
  │         │         │
  │         │         └── No → [Notify customer: insufficient balance]
  │         │                   |
  │         │                   {Customer action?}
  │         │                   ├── Top up → [Retry pre-auth]
  │         │                   └── Cancel → [Order not created]
  │         │
  │         └── No → [Highlight missing fields to customer]
  │                   |
  │                   ←─ [Return to item entry]
  │
  └── No → [Return to order review]
            |
            ⏹️ End — Order not submitted
```

### Business Rules Cross-Reference

| Step | Business Rules |
|------|----------------|
| Select delivery location | D-001, E-005 |
| Enter items | D-002 (optional brand, unit, max price) |
| Shopping preference | D-001 |
| Substitution preference | D-007 |
| Price breakdown | D-003, F-001, F-002 |
| Validate requirements | D-001 |
| Pre-auth hold | F-003, F-011 |
| Order created | D-004 (→ Awaiting Payment Verification state) |

---

## 3. Process 2 — Shopper Acceptance & Assignment

### Scope
From order queued through the assignment cascade to acceptance or timeout.

### Swimlanes
- **Platform System (Assignment Engine)** — manages the offer cascade
- **Shopper** — receives and responds to offers
- **Customer** — receives status updates

### Process Diagram (Text)

```
→ [DMZ from Process 1: Order Queued for Assignment]

Swimlane: Platform System (Assignment Engine)
    |
    [Calculate Assignment Scores for all eligible shoppers in zone (C-011)]
    |
    [Rank shoppers by score]
    |
    [Select highest-scoring shopper]
    |
    [Verify shopper is within zone boundary (I-003)]
    |
    ⚡ [Send offer to shopper: push + sound + in-app alert (J-003)]
    |
    [Start 30-second timer]
    |
    {Shopper responds within 30s?}
    │
    ├── Yes → {Response = Accept or Decline?}
    │         │
    │         ├── Accept →
    │         │         |
    │         │    Swimlane: Shopper
    │         │         [Tap "Accept"]
    │         │         |
    │         │    Swimlane: Platform System
    │         │         [Release shopper's GPS at 5-second frequency (C-001)]
    │         │         [Send route to market (D-005)]
    │         │         [Calculate dynamic Expected Arrival Time]
    │         │         [Notify customer: shopper assigned + ETA (J-004)]
    │         │         |
    │         │    Swimlane: Customer
    │         │         [See shopper name, photo, rating, general area]
    │         │         |
    │         │         → [DMZ: Process 3 — Shopping Workflow]
    │         │         |
    │         │         ⏹️ End — Order accepted
    │         │
    │         └── Decline →
    │                   [Record decline (B-011)]
    │                   |
    │                   ↓
    │              Swimlane: Platform System
    │                   {More eligible shoppers?}
    │                   ├── Yes → [Select next highest-scoring shopper]
    │                   │         |
    │                   │         ←─ [Return to offer loop]
    │                   │
    │                   └── No → [See "No shoppers left" path below]
    │
    └── No → (30s elapsed)
              [Treat as decline (C-003)]
              [Record timeout in acceptance rate (B-011)]
              |
              {More eligible shoppers AND < 3 minutes total?}
              ├── Yes → [Select next highest-scoring shopper]
              │         |
              │         ←─ [Return to offer loop]
              │
              └── No → (3-minute cascade elapsed)
                        [Flag order as "Unassigned" (C-004)]
                        |
                        Swimlane: Customer
                        [Notify customer with options (D-010)]
                        |
                        {Customer chooses?}
                        ├── Try again → [Restart cascade]
                        ├── Expand radius → [Update zone radius, restart cascade]
                        ├── Increase fee → [Update delivery fee, restart cascade]
                        └── Cancel → [Order cancelled. Release pre-auth (G-001)]
                                      |
                                      ⏹️ End — Order cancelled
```

### Cascade Timer Detail

```
Offer sent to Shopper #1
│
├── 0s to 30s: Shopper #1 window
│   ├── Accept → assigned
│   └── Decline/Timeout →
│
├── 30s to 60s: Shopper #2 window (next highest score)
│   ├── Accept → assigned
│   └── Decline/Timeout →
│
├── 60s to 90s: Shopper #3 window
│   └── ...continues until...
│
└── 180s (3 minutes): Cascade timeout
    └── Customer notified with options
```

### Business Rules Cross-Reference

| Step | Business Rules |
|------|----------------|
| Assignment Score calculation | C-011, B-011, B-008, H-004 |
| Zone verification | C-005, I-003 |
| 30-second window | C-003 |
| Cascade logic | C-004 |
| Acceptance rate tracking | B-011 |
| Customer notification | J-004 |
| Options on timeout | D-010 |

---

## 4. Process 3 — Shopping Workflow

### Scope
From shopper arrival at market through item selection, substitutions, checkout, and receipt upload.

### Swimlanes
- **Shopper** — physically shops items
- **Customer** — receives updates and substitution requests
- **Platform System** — manages item tracking and timing

### Process Diagram (Text)

```
→ [DMZ from Process 2: Order Accepted]

Swimlane: Shopper
    [Travel to market location]
    |
    [Tap "Arrived at Market" OR GPS confirms arrival]
    |
Swimlane: Platform System
    [Verify GPS against market location (D-005)]
    |
    {GPS matches market zone?}
    ├── Yes → [Start shopping timer (D-011 — tiered by item count)]
    │         |
    │    Swimlane: Shopper
    │         [Begin shopping — locate each item]
    │         |
    │         For each item on list:
    │         |
    │         {Item found?}
    │         ├── Yes → [Mark as "Found"]
    │         │         [Optionally upload photo (quality, damage)]
    │         │         |
    │         │         ↓ Next item
    │         │
    │         ├── No → {Substitution preference?}
    │         │        ├── Best Match → [Select similar item (price + quality)]
    │         │        │                 [Mark as "Substituted"]
    │         │        │                 ↓ Next item
    │         │        │
    │         │        ├── Contact Me → [Send chat message with options + photo]
    │         │        │                 ⚡ Wait for response (configurable timeout, default 3 min)
    │         │        │                 |
    │         │        │                 {Customer responds?}
    │         │        │                 ├── Yes → Customer: [Approve / Decline / Suggest alternative]
    │         │        │                 │         ├── Approve → [Mark as "Substituted"]
    │         │        │                 │         └── Decline → [Mark as "Not Available"]
    │         │        │                 │
    │         │        │                 └── No → (timeout)
    │         │        │                           [Follow fallback policy: skip or Best Match]
    │         │        │                           ↓ Next item
    │         │        │
    │         │        └── No Substitutions → [Mark as "Not Available"]
    │         │                                ↓ Next item
    │         │
    │         After all items processed:
    │         [Proceed to checkout]
    │         [Pay vendor (shopper uses own funds)]
    │         [Request receipt]
    │         |
    │         {Receipt available?}
    │         ├── Single itemised → [Upload photo]
    │         ├── Multiple receipts → [Upload all photos]
    │         ├── Handwritten → [Upload photo]
    │         └── No receipt → [Manually enter prices per item (D-008)]
    │         |
    │         [Tap "Shopping Complete"]
    │         |
    │    Swimlane: Platform System
    │         [Validate receipt (format check)]
    │         |
    │         {Receipt valid?}
    │         ├── Yes → [Calculate final amounts (F-004)]
    │         │         |
    │         │         {Final > estimate by 10% OR 10,000 TZS?}
    │         │         ├── Yes → [Request customer approval]
    │         │         │         |
    │         │         │    Swimlane: Customer
    │         │         │         {Approve?}
    │         │         │         ├── Yes → [Continue]
    │         │         │         └── No/Timeout → [Process at original estimate, platform covers overshoot]
    │         │         │
    │         │         └── No → [Continue]
    │         │
    │         [Mark order status: Receipt Verified]
    │         |
    │         → [DMZ: Process 4 — Delivery & Fulfillment]
    │
    └── No → (GPS mismatch)
              [Flag for manual review. Shopper may override with note.]
              [Continue if shopper is at correct location]
```

### Item Timer Monitoring

```
Shopping timer runs based on item count (D-011):
  ┌────────────────────┬─────────────┬──────────────────────────────┐
  │ Order Size         │ Expected    │ Alert (+50%) / Support (+100%)│
  ├────────────────────┼─────────────┼──────────────────────────────┤
  │ Small (≤ 10 items) │ 30-45 min   │ Alert at 45-68 min           │
  │ Medium (11-20)     │ 45-60 min   │ Alert at 68-90 min           │
  │ Large (> 20 items) │ 60-90 min   │ Alert at 90-135 min          │
  └────────────────────┴─────────────┴──────────────────────────────┘
```

### Business Rules Cross-Reference

| Step | Business Rules |
|------|----------------|
| Arrival verification | D-005 |
| Item found confirmation | D-006 |
| Substitution rules | D-007 |
| Shopping timer | D-011 |
| Receipt upload | D-008 |
| Final amount calculation | F-004 |
| Price variation approval | D-003 |

---

## 5. Process 4 — Delivery & Fulfillment

### Scope
From receipt verified through delivery to customer confirmation and order completion.

### Swimlanes
- **Shopper** — delivers items
- **Customer** — receives items, inspects, confirms
- **Platform System** — tracks progress, manages ETA, handles exceptions

### Process Diagram (Text)

```
→ [DMZ from Process 3: Receipt Verified]

Swimlane: Shopper
    [Begin travel to delivery location]
    |
Swimlane: Platform System
    [Start delivery timer (E-003 — dynamic ETA)]
    [Start GPS route tracking at 5-second frequency (C-001)]
    [Share ETA with customer]
    |
    ⚡ Continuous ETA monitoring (E-009):
        {ETA change > 5 min?}
        ├── Yes → [Recalculate ETA]
        │         [Notify customer with reason]
        │         [Suggest rerouting to shopper if needed]
        └── No → [Continue monitoring]
    |
Swimlane: Shopper
    [Arrive at delivery location]
    [Tap "Arrived"]
    |
Swimlane: Platform System
    [Verify GPS within 100m of delivery address (E-002)]
    |
    {GPS matches?}
    ├── Yes → [Continue]
    └── No → [Shopper may override with note. Logged for audit.]
    |
Swimlane: Shopper
    [Contact customer via masked call or chat (J-002)]
    |
    {Customer available?}
    ├── Yes →
    │    [Hand over items to customer / Authorized Recipient]
    │    [Take delivery photo (E-002)]
    │    |
    │    Swimlane: Customer
    │         [Confirm receipt in app]
    │         |
    │         ⚡ [Start inspection window (E-007 — category-based)]
    │         |
    │         {Items satisfactory?}
    │         ├── Yes → [Tap "Everything is correct"]
    │         │         |
    │         │    Swimlane: Platform System
    │         │         [Mark order as "Completed"]
    │         │         [Trigger payment capture (F-004)]
    │         │         [Start 48-hour settlement timer (F-006)]
    │         │         [Prompt ratings (H-001, H-002)]
    │         │         |
    │         │         → [DMZ: Process 5 — Refund Processing (if issues)]
    │         │         ⏹️ End — Order completed successfully
    │         │
    │         └── No → [Report issue in app]
    │                   |
    │                   → [DMZ: Process 6 — Dispute Resolution]
    │
    └── No → (Customer unavailable)
              [Attempt masked call]
              [Send chat message]
              ⚡ Wait 5 minutes (E-004)
              |
              {Customer responded?}
              ├── Yes → [Return to delivery handover]
              │
              └── No →
                    {Customer pre-authorised safe drop?}
                    ├── Yes → [Leave items at safe location]
                    │         [Take photo]
                    │         [Mark as "Delivered — Safe Drop"]
                    │
                    └── No → [Return items to market]
                              [Notify support]
                              [Offer redelivery (E-008) or cancellation refund (G-003)]
```

### Inspection Windows (E-007)

```
┌─────────────────────────┬──────────┐
│ Order Category          │ Window   │
├─────────────────────────┼──────────┤
│ Fresh produce, dairy    │ 5 min    │
│ Fresh meat, fish        │ Immediate│
│ Electronics, packaged   │ 24 hours │
│ Non-food general goods  │ 30 min   │
│ Mixed                   │ Per item │
└─────────────────────────┴──────────┘
```

### Business Rules Cross-Reference

| Step | Business Rules |
|------|----------------|
| Delivery timer | E-003 |
| GPS route tracking | C-001, E-009 |
| ETA monitoring | E-009 |
| Proof of delivery | E-002 |
| Customer unavailable | E-004 |
| Wrong address | E-005 |
| Inspection window | E-007 |
| Redelivery | E-008 |
| Payment capture | F-004 |
| Settlement | F-006 |
| Ratings | H-001, H-002 |

---

## 6. Process 5 — Refund Processing

### Scope
From refund request through validation, approval, execution, and notification.

### Swimlanes
- **Customer** — requests refund
- **Platform System** — validates and processes
- **Support Agent** — reviews if needed
- **Shopper** — notified of outcome
- **Mobile Money Provider** — processes transfer

### Process Diagram (Text)

```
⭕ Start — Refund trigger
    │
    (Triggered by: customer dispute, platform fault, or cancellation fee)
    │
Swimlane: Platform System
    [Determine refund type and amount per policy]
    |
    {Refund type?}
    │
    ├── Full Refund (Platform Fault — G-006)
    │   [Calculate: total customer charge]
    │   [Log incident for root cause analysis]
    │
    ├── Partial Refund (Item Issue — G-007)
    │   {Severity?}
    │   ├── Minor → [Refund: affected item value only]
    │   ├── Moderate → [Refund: affected items + 10% delivery fee]
    │   └── Major → [Refund: full order OR affected items + 30% delivery fee]
    │
    ├── Cancellation Fee Refund (G-001, G-002, G-003)
    │   [Calculate: pre-auth release minus applicable fee]
    │   [Shopper compensation: per stage (F-011)]
    │
    └── Goodwill Refund (G-009 — discretionary)
        [Support Agent assesses case]
        {Level?}
        ├── Minor inconvenience → [Refund only, no goodwill]
        ├── Moderate + platform contribution → [Refund + discretionary 5-10%]
        └── Significant failure → [Refund + goodwill (up to 15%, capped 10k TZS)]
        │
        {Goodwill > 10,000 TZS?}
        ├── Yes → [Requires Ops Admin approval]
        └── No → [Support Agent can approve directly]
    │
    ↓
    [Validate refund against policy rules]
    |
    {Valid?}
    ├── Yes → [Queue refund for processing]
    │         ⚡ [Process payment to customer's original payment method]
    │         |
    │         {Processing success?}
    │         ├── Yes → [Notify customer: refund processed]
    │         │         [Notify shopper: if deduction from earnings applied]
    │         │         [Log transaction in audit log (L-002)]
    │         │         |
    │         │         ⏹️ End — Refund completed
    │         │
    │         └── No → [Retry up to 2 times within 5 min (F-011)]
    │                   {Success on retry?}
    │                   ├── Yes → [Complete refund]
    │                   └── No → [Escalate to support for manual processing]
    │
    └── No → [Reject refund request with reason]
              [Notify customer with appeal rights (G-009)]
              |
              ⏹️ End — Refund rejected
```

### Refund Timelines (G-008)

```
┌──────────────────────────────────────┬────────────────┐
│ Scenario                             │ Target Time    │
├──────────────────────────────────────┼────────────────┤
│ Automated refund (< 10,000 TZS)      │ < 2 hours      │
│ Manual review refund                 │ < 24 hours     │
│ Platform fault refund (G-006)        │ < 2 hours      │
│ Disputed refund (requires escalation)│ < 24 hours     │
│ Post-settlement refund               │ < 48 hours     │
└──────────────────────────────────────┴────────────────┘
```

### Business Rules Cross-Reference

| Step | Business Rules |
|------|----------------|
| Full refund (platform fault) | G-006 |
| Partial refund (item issues) | G-007 |
| Cancellation refund | G-001, G-002, G-003 |
| Shopper protection | F-011 |
| Discretionary goodwill | G-009 |
| Refund timeline | G-008 |
| Audit logging | L-002 |
| Failed payment retry | F-011 |

---

## 7. Process 6 — Dispute Resolution

### Scope
From issue report through evidence collection, triage, decision, and closure.

### Swimlanes
- **Customer** — reports issue
- **Shopper** — may be involved
- **Platform System** — triages and auto-resolves straightforward cases
- **Support Agent** — handles standard manual review
- **Operations Admin** — handles complex/escalated cases

### Process Diagram (Text)

```
⭕ Start — Issue reported (by Customer, Shopper, or System)
    |
Swimlane: Platform System
    [Assign dispute ID]
    [Gather available evidence: order data, chat logs, GPS trace, receipt]
    |
    {Dispute type?}
    │
    ├── Shopper Behaviour (K-007)
    │   ⚡ [Immediately suspend shopper pending investigation]
    │   [Bypass automated validation]
    │   [Route directly to Operations Admin]
    │
    ├── High Value ( > 100,000 TZS)
    │   [Bypass automated validation]
    │   [Route directly to Operations Admin]
    │
    └── Standard (Item issue, payment, cancellation)
        |
        {Eligible for automated validation?}
        ├── Yes → (Criteria: < 10,000 TZS, clear evidence, straightforward)
        │         [Apply automated resolution per policy (G-007 tiers)]
        │         |
        │         {Resolution determined?}
        │         ├── Refund → [Execute refund per Process 5]
        │         ├── Reject → [Notify customer with rationale + appeal rights]
        │         └── Partial → [Execute partial refund]
        │         |
        │         ⚡ [Log resolution]
        │         |
        │         {Customer satisfied?}
        │         ├── Yes → [Close dispute]
        │         │         ⏹️ End — Resolved
        │         └── No → [Customer requests manual review (G-009)]
        │                   ↓
        │                   Route to Manual Review
        │
        └── No → Route to Manual Review
                  |
                  ↓
            Swimlane: Support Agent
                  [Review case — evidence, chat logs, receipt, GPS, history]
                  |
                  {Additional evidence needed?}
                  ├── Yes → [Request evidence from relevant party]
                  │         ⚡ Wait 24 hours for response
                  │         |
                  │         {Evidence received?}
                  │         ├── Yes → [Continue review]
                  │         └── No → [Decide based on available evidence]
                  │
                  └── No → [Proceed to decision]
                            |
                            {Decision?}
                            ├── Shopper Error → [Process refund per G-007]
                            │                    [Update shopper quality metrics]
                            │                    {Pattern of errors?}
                            │                    ├── Yes → [Trigger B-007 progressive discipline]
                            │                    └── No → [Close case]
                            │
                            ├── Customer Error → [Reject claim with explanation]
                            │                    [Notify customer of appeal rights]
                            │
                            ├── Inconclusive → [Discretionary goodwill per G-009]
                            │                  [Close case]
                            │
                            └── Platform Error → [Full refund (G-006)]
                                                  [Log root cause (G-006)]
                                                  {3+ same fault in 30 days?}
                                                  ├── Yes → [Trigger engineering review]
                                                  └── No → [Close case]
                            |
                            ↓
                      ⚡ [Notify both parties of decision with rationale]
                      ⚡ [Execute financial adjustments]
                      ⚡ [Log complete case for analytics (L-002)]
                            |
                            {Shopper appeals?}
                            ├── Yes → (B-012)
                            │         [24h acknowledgment]
                            │         [5 business days final decision]
                            └── No → [Close dispute]
                                      ⏹️ End — Dispute closed
```

### Dispute Triage Matrix

```
┌──────────────────────┬───────────────┬──────────────┬────────────────┐
│ Criteria             │ Route         │ Assignee     │ SLA Target     │
├──────────────────────┼───────────────┼──────────────┼────────────────┤
│ < 10,000 TZS         │ Automated     │ System       │ < 1 hour       │
│ Clear evidence       │               │              │                │
├──────────────────────┼───────────────┼──────────────┼────────────────┤
│ 10,000-100,000 TZS   │ Manual Review │ Support Agent│ < 24 hours     │
│ Standard complexity  │               │              │                │
├──────────────────────┼───────────────┼──────────────┼────────────────┤
│ > 100,000 TZS        │ Escalated     │ Ops Admin    │ < 24 hours     │
│ OR contradictory      │               │              │                │
├──────────────────────┼───────────────┼──────────────┼────────────────┤
│ Shopper behaviour     │ Escalated     │ Ops Admin    │ < 48 hours     │
│ (harassment, abuse)  │ (immediate    │              │ (decision)     │
│                       │  suspension)  │              │                │
├──────────────────────┼───────────────┼──────────────┼────────────────┤
│ Party requests review │ Escalated     │ Ops Admin    │ < 24 hours     │
│ of automated decision │               │              │                │
└──────────────────────┴───────────────┴──────────────┴────────────────┘
```

### Business Rules Cross-Reference

| Step | Business Rules |
|------|----------------|
| Dispute lifecycle | G-011 |
| Automated triage | L-008 |
| Shopper behaviour | K-007 |
| Partial refund (item issues) | G-007 |
| Full refund (platform fault) | G-006 |
| Discretionary goodwill | G-009 |
| Shopper progressive discipline | B-007 |
| Shopper appeal | B-012 |
| Audit logging | L-002 |
| Evidence: chat logs | I-010 |
| Evidence: GPS logs | L-003 |

---

*This document is Phase 5 of the Urban Shopper Platform specification. These BPMN descriptions can be directly translated into visual BPMN diagrams using tools such as Camunda Modeler, Lucidchart, or draw.io. They feed into Phase 7 (System Architecture) and Phase 8 (IEEE 29148 SRS).*
