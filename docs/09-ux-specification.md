# Urban Shopper Platform — UX Specification

> **Document Type:** UX Design Specification  
> **Status:** Complete  
> **Date:** 2026-07-24  
> **Version:** 1.0  
> **Cross-Reference:** Domain Model — `02-domain-model.md`, Business Rules — `03-business-rules.md`, SRS — `08-ieee-29148-srs.md`  
> **Phase:** 9 of 12

---

## Table of Contents

1. [Design Principles](#1-design-principles)
2. [Customer App — Screen Map](#2-customer-app--screen-map)
3. [Customer App — Screen Specifications](#3-customer-app--screen-specifications)
4. [Shopper App — Screen Map](#4-shopper-app--screen-map)
5. [Shopper App — Screen Specifications](#5-shopper-app--screen-specifications)
6. [Admin Dashboard — Screen Map](#6-admin-dashboard--screen-map)
7. [Admin Dashboard — Screen Specifications](#7-admin-dashboard--screen-specifications)
8. [Shared Components](#8-shared-components)
9. [Error States](#9-error-states)
10. [Loading States](#10-loading-states)
11. [Empty States](#11-empty-states)
12. [Push Notification Templates](#12-push-notification-templates)

---

## 1. Design Principles

| # | Principle | Application |
|---|-----------|-------------|
| UX-1 | **Mobile money first** — M-Pesa/Mixx as primary payment method. Card is supplementary. | Payment screens default to mobile money. Card option is secondary. |
| UX-2 | **Low-bandwidth ready** — Compressed images, minimal payload, offline queues. | App size ≤ 25 MB. Data usage ≤ 5 MB per 10-min session. Offline order status available. |
| UX-3 | **Swahili + English** — Full bilingual support. System language matches device setting, changeable in-app. | Every screen, error message, and notification available in both languages. |
| UX-4 | **Big, clear actions** — 48x48dp minimum touch targets. High-contrast colours. Large fonts. | Especially critical for the shopper app used on budget Android devices. |
| UX-5 | **Countdown visibility** — The 30-second offer timer is the most important UI element in the shopper app. | Prominent countdown with sound + vibration. Must be visible even if the app is in the background (via persistent notification). |
| UX-6 | **Trust through transparency** — Show prices before commitment. Show shopper ratings and verification status. Show real-time tracking. | Price breakdown before order submission. Shopper profile with rating and badge visible after assignment. GPS tracking during delivery. |
| UX-7 | **Progressive disclosure** — Don't overwhelm users with options. Show what's needed at each step. | Item entry with optional fields revealed only if needed. Substitution preferences set once at order level with per-item override. |

---

## 2. Customer App — Screen Map

```
┌──────────────────────────────────────────────────────────┐
│                    CUSTOMER APP                          │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  ONBOARDING                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────┐   │
│  │ Splash/Intro  │─>│ Phone Entry  │─>│ OTP Verify  │   │
│  └──────────────┘  └──────────────┘  └──────┬───────┘   │
│                                             │           │
│                                             ▼           │
│                                       ┌──────────────┐  │
│                                       │Profile Setup  │  │
│                                       │(Name, Lang,   │  │
│                                       │ Notif Prefs)  │  │
│                                       └──────┬───────┘  │
│                                              │          │
│  ┌───────────────────────────────────────────┘          │
│  ▼                                                        │
│  HOME SCREEN                                              │
│  ┌─────────────────────────────────────────────────────┐ │
│  │  Header: Logo, Notification Bell, Profile Icon      │ │
│  │  ───────────────────────────────────────────────── │ │
│  │  Active Order Card (if order in progress)           │ │
│  │  ───────────────────────────────────────────────── │ │
│  │  Quick Actions: [New Order] [Scheduled] [Reorder]   │ │
│  │  ───────────────────────────────────────────────── │ │
│  │  Recent Orders (last 5)                             │ │
│  │  ───────────────────────────────────────────────── │ │
│  │  Promo Banner (first-order discount, referrals)    │ │
│  └─────────────────────────────────────────────────────┘ │
│                           │                              │
│         ┌─────────────────┼──────────────────┐           │
│         ▼                 ▼                  ▼           │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │ New Order   │  │ Order Detail │  │ Profile      │    │
│  │ Flow        │  │ + Tracking   │  │ Settings     │    │
│  └─────────────┘  └──────────────┘  └──────────────┘    │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### New Order Flow (Detailed)

```
New Order Flow:
┌────────────┐   ┌────────────┐   ┌────────────┐   ┌────────────┐
│            │   │            │   │            │   │            │
│ 1. Pick    │──>│ 2. Add     │──>│ 3. Review  │──>│ 4. Order   │
│ Delivery   │   │ Items      │   │ & Confirm  │   │ Placed     │
│ Location   │   │            │   │            │   │            │
│            │   │            │   │            │   │            │
└────────────┘   └────────────┘   └────────────┘   └────────────┘
                                                          │
                                                          ▼
┌────────────┐   ┌────────────┐   ┌────────────┐   ┌────────────┐
│            │   │            │   │            │   │            │
│ 5. Tracking│   │ 6. Delivery│   │ 7. Rate    │   │ 8. Order   │
│ Live View  │──>│ Confirmed  │──>│ Shopper    │──>│ Complete   │
│            │   │            │   │            │   │            │
│            │   │            │   │            │   │            │
└────────────┘   └────────────┘   └────────────┘   └────────────┘
```

---

## 3. Customer App — Screen Specifications

### Screen 3.1: Phone Entry

| Element | Specification |
|---------|---------------|
| **Header** | "Welcome to Urban Shopper" (Swahili: "Karibu Urban Shopper") |
| **Phone Input** | Country code pre-selected (+255). Numeric keypad. Format: XXX XXX XXX |
| **CTA Button** | "Send Verification Code" — disabled until 10 digits entered |
| **Language Toggle** | Swahili / English — at bottom of screen |
| **Legal** | "By continuing, you agree to our Terms of Service" — tappable link |
| **Error State** | "Invalid phone number. Please check and try again." |
| **Network Error** | "Unable to connect. Please check your internet connection." |

### Screen 3.2: OTP Verification

| Element | Specification |
|---------|---------------|
| **Header** | "Enter Verification Code" / "We sent a code to +255 XXX XXX XXX" |
| **OTP Input** | 6-digit input, auto-advance on each digit. Paste support. |
| **Timer** | "Resend code in 00:45" — countdown from 60s. Tap to resend after expiry. |
| **CTA Button** | "Verify" — auto-submits when 6 digits entered |
| **Alternate** | "Call me with the code" — triggers voice call fallback |
| **Error State** | "Incorrect code. X attempts remaining." After 3 failures: "Too many attempts. Please try again in 15 minutes." |

### Screen 3.3: Profile Setup

| Element | Specification |
|---------|---------------|
| **Header** | "Complete Your Profile" |
| **Full Name** | Text input. Min 2 characters, max 50. |
| **Language** | Toggle: Swahili / English |
| **Notification Preferences** | Checkboxes: Push (default on), SMS (default on), In-App (default on). Note: "Emergency notifications are always sent via SMS." |
| **CTA Button** | "Continue" |
| **Terms Checkbox** | "I accept the Terms of Service and Privacy Policy" — must be checked. |

### Screen 3.4: Home Screen

| Element | Specification |
|---------|---------------|
| **Active Order Card** | If order in progress: shows status, shopper name, ETA. Tap opens tracking. Priority: always visible above fold. |
| **Quick Actions** | "New Order" (primary), "My Orders" (secondary), "Schedule" (future) |
| **Promo Banner** | First-order: "Get 50% off your first delivery!" Tap activates. Referral: "Invite a friend — you both get TZS 5,000!" |
| **Recent Orders** | Last 5 orders: date, status, total. Tap for details. |
| **Bottom Navigation** | Home, Orders, Wallet (V1), Profile |

### Screen 3.5: New Order — Location

| Element | Specification |
|---------|---------------|
| **Header** | "Where should we deliver?" |
| **Map Pin-Drop** | Interactive map. Customer drags pin to exact location. Default: current GPS location if available. |
| **Address Text** | Auto-reverse geocode from pin. Customer can edit/free-type address. |
| **Landmark** | Optional: "Near Kariakoo Market", "Next to Mlimani City" |
| **Save Address** | Toggle: "Save this address for future orders" |
| **CTA Button** | "Confirm Location" |

### Screen 3.6: New Order — Add Items

| Element | Specification |
|---------|---------------|
| **Header** | "What do you need?" |
| **Item Input** | Text field with predictive text suggestions based on popular items. |
| **Item Card** | Each added item shows: name, quantity, optional brand, optional unit, optional max price. Editable. Swipe to delete. |
| **Shopping Preference** | Radio: Cheapest Available / Best Quality / Balanced. Sets the default substitution strategy. |
| **Add Button** | "+ Add Another Item" — adds new blank row |
| **CTA Button** | "Continue to Review" (enabled when ≥ 1 item) |

### Screen 3.7: New Order — Review & Confirm

| Element | Specification |
|---------|---------------|
| **Header** | "Review Your Order" |
| **Item Summary** | List of all items with quantities and notes |
| **Shopping Preference** | Displayed: "Shopping preference: Best Quality" |
| **Delivery Address** | Displayed with map thumbnail |
| **Delivery Time** | Toggle: "ASAP" / "Schedule" — if schedule, date/time picker opens |
| **Price Breakdown** | Card showing: item estimate, service fee (with tier %), delivery fee, **total** |
| **Payment Method** | Radio: M-Pesa / Mixx / Cash on Delivery (if eligible). Mobile money shows balance after selection. |
| **Substitution Default** | Per-item: Best Match / Contact Me / No Substitutions |
| **Terms** | "By placing this order, you agree to the fee structure" |
| **CTA Button** | "Place Order" — triggers pre-auth and submission |

### Screen 3.8: Order Tracking

| Element | Specification |
|---------|---------------|
| **Header** | "Order #URB-XXXX" |
| **Status Timeline** | Visual progress bar showing all order states. Current state highlighted. Completed states checkmarked. |
| **Shopper Card** | (After acceptance) Shopper photo, name, rating stars, "X orders completed" |
| **ETA Display** | "Your shopper will arrive in ~15 minutes" — updates dynamically |
| **Live Map** | (V1) Shopper GPS location on map with route to delivery address |
| **Chat Button** | "Message Shopper" — opens in-app chat |
| **Call Button** | "Call Shopper" — masked call |
| **Cancel Button** | "Cancel Order" — shows applicable fee before confirmation |
| **Status Messages** | Contextual: "Finding a shopper..." / "Juma is heading to Kariakoo Market" / "Juma is shopping your items (5 of 12 found)" / "Receipt verified ✓" / "Juma is on their way!" / "Arrived!" |

### Screen 3.9: Rate Shopper

| Element | Specification |
|---------|---------------|
| **Header** | "How was your experience?" |
| **Shopper Info** | Shopper photo, name |
| **Star Rating** | 5 stars, tappable. Tapping a star highlights it and all previous. |
| **Criteria Breakdown** | (V1) Expandable: Item Accuracy, Item Quality, Timeliness, Communication, Professionalism |
| **Feedback Field** | Optional text: "Anything else? (optional)" |
| **CTA Button** | "Submit Rating" |

### Screen 3.10: Profile & Settings

| Element | Specification |
|---------|---------------|
| **Header** | "My Profile" |
| **Name** | Editable |
| **Phone** | Displayed (cannot change without re-verification) |
| **Language** | Toggle: Swahili / English |
| **Notification Preferences** | Push, SMS, In-App toggles |
| **Saved Addresses** | List of saved delivery locations. Add/edit/delete. |
| **Customer Trust Score** | Displayed (V1) — "Your Trust Score: 85/100" |
| **Promo Credits** | Balance of referral credits |
| **Delete Account** | "Delete My Account" — requires confirmation, blocks if orders pending |
| **App Version** | Displayed at bottom |

### Screen 3.11: Order History

| Element | Specification |
|---------|---------------|
| **Header** | "My Orders" |
| **Filter Tabs** | All / Active / Completed / Cancelled |
| **Order Card** | Each order: ID, date, item count, total, status, shopper name |
| **Tap** | Opens Order Detail with full tracking history |
| **Reorder Button** | "Order Again" — pre-fills items from that order |
| **Pagination** | Infinite scroll, 20 orders per page |

---

## 4. Shopper App — Screen Map

```
┌──────────────────────────────────────────────────────────┐
│                    SHOPPER APP                           │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  ONBOARDING                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ Register     │─>│ Verify ID    │─>│ Transport    │  │
│  │ (Phone + OTP)│  │ (ID photo +  │  │ Type + Docs  │  │
│  └──────────────┘  │ selfie)      │  └──────┬───────┘  │
│                    └──────┬───────┘         │           │
│                           ▼                 ▼           │
│                    ┌──────────────┐  ┌──────────────┐  │
│                    │ Banking      │  │ Emergency    │  │
│                    │ (M-Pesa for  │  │ Contact +    │  │
│                    │  payouts)    │  │ Code of      │  │
│                    └──────┬───────┘  │ Conduct     │   │
│                           │          └──────┬───────┘  │
│                           ▼                 ▼           │
│                    ┌──────────────┐  ┌──────────────┐  │
│                    │ Pending      │─>│ Onboarding   │  │
│                    │ Approval     │  │ Training +   │  │
│                    │ Screen       │  │ Assessment   │  │
│                    └──────────────┘  └──────────────┘  │
│                                                          │
│  ┌─────────────────────────────────────────────────────┐ │
│  │                 HOME SCREEN                          │ │
│  │  ┌─────────────────────────────────────────────────┐│ │
│  │  │ Status Card: 🟢 Online (tap to go Offline)      ││ │
│  │  │ Earnings Today: TZS X,XXX                        ││ │
│  │  │ Orders Today: X                                  ││ │
│  │  │ Rating: ⭐ X.X (X ratings)                       ││ │
│  │  ├─────────────────────────────────────────────────┤│ │
│  │  │ [Go Online] / [Go Offline] — large toggle       ││ │
│  │  ├─────────────────────────────────────────────────┤│ │
│  │  │ Active Order Card (if on an order)               ││ │
│  │  └─────────────────────────────────────────────────┘│ │
│  └─────────────────────────────────────────────────────┘ │
│                           │                              │
│         ┌─────────────────┼──────────────────┐           │
│         ▼                 ▼                  ▼           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │ Order Flow   │  │ Earnings     │  │ Profile      │   │
│  │ (5 screens)  │  │ Dashboard    │  │ Settings     │   │
│  └──────────────┘  └──────────────┘  └──────────────┘   │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### Order Acceptance Flow (The Critical Path)

```
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│ Offer        │   │ Offer        │   │ Order        │
│ Received     │──>│ Detail       │──>│ Accepted     │
│ (Countdown)  │   │ (View Items  │   │ (Route to    │
└──────────────┘   │  + Pay)      │   │  Market)     │
                   └──────────────┘   └──────┬───────┘
                                             │
              ┌──────────────────────────────┘
              ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│ Arrived at   │──>│ Shopping     │──>│ Receipt      │
│ Market       │   │ (Item List   │   │ Upload       │
│ (Confirm GPS)│   │  + Status    │   └──────┬───────┘
└──────────────┘   │  + Substitut)│          │
                   └──────────────┘          ▼
                                     ┌──────────────┐
                                     │ Deliver      │
                                     │ (Navigation  │
                                     │  + Photo +   │
                                     │  Confirm)    │
                                     └──────────────┘
```

---

## 5. Shopper App — Screen Specifications

### Screen 5.1: Home / Online Toggle

| Element | Specification |
|---------|---------------|
| **Online Status** | Large pill toggle: 🟢 "You're Online — TZS X/hr potential" / 🔴 "You're Offline" |
| **Earnings Today** | "Today's Earnings: TZS X,XXX" — updates after each completed order |
| **Orders Today** | Count |
| **Rating** | Current rating with trend indicator (↑ / ↓) |
| **Active Order Card** | (When on an order) Full-width card showing current status, customer name, ETA. Tap opens order flow. Bright colour to differentiate from idle state. |

### Screen 5.2: Offer Received (Countdown)

**CRITICAL SCREEN — This is the most important screen in the shopper app.**

| Element | Specification |
|---------|---------------|
| **Overlay** | Full-screen overlay that appears regardless of current app view. Cannot be dismissed without accepting or timing out. |
| **Countdown Timer** | Large, centre-screen: **25** (seconds). Circular progress arc. Turns red at 10 seconds. |
| **Vibration** | Device vibrates in 3 short pulses on offer receipt |
| **Sound** | Configurable alert sound (default on). Option to mute in settings. |
| **Order Summary** | Item count (e.g., "12 items"), market location (e.g., "Kariakoo Market"), estimated distance, estimated pay |
| **Customer General Area** | Neighbourhood (e.g., "Mikocheni"), distance from market (e.g., "6 km") |
| **Actions** | Two large buttons: [ACCEPT ✅] (green, 70% width) / [DECLINE ❌] (grey, 30% width) |
| **Persistent Notification** | If app is in background, a persistent notification with countdown appears in the notification tray. |
| **Timeout State** | When timer hits 0, overlay dismisses automatically. Short vibration burst indicating offer expired. |

### Screen 5.3: Order Progress (Multi-Step)

| Element | Specification |
|---------|---------------|
| **Progress Bar** | 5-step visual: 🛒 Travel → Shop → Receipt → Deliver → Done. Current step highlighted. |
| **Step 1: Travel** | Map showing route to market. "Arrived at Market" button. GPS auto-confirm optional. |
| **Step 2: Shop Items** | Item list with status buttons per item. Each item card: name, quantity, optional fields, 3 action buttons: [Found ✅] [Substitute 🔄] [Not Available ❌]. Substitution opens chat contact flow. |
| **Note** | After marking item as Substituted or Not Available — the next unhandled item auto-scrolls into view. |
| **Step 3: Receipt** | "Upload Receipt" — launches camera. Supports multiple photos. "No receipt — enter prices manually" fallback. |
| **Step 4: Deliver** | Map showing route to customer. "Arrived" button. Delivery photo capture. Customer confirmation. |
| **Step 5: Complete** | "Order Complete! Earnings: TZS X,XXX added to your wallet." Rate customer button. |

### Screen 5.4: Chat

| Element | Specification |
|---------|---------------|
| **Header** | Customer name, order ID |
| **Message List** | Scrollable chat history. Sent messages right-aligned (green). Received left-aligned (grey). |
| **Input** | Text field + send button. |
| **Quick Replies** | (V1) Pre-written: "I'm at the market", "This item isn't available", "I found a substitute", "I'm on my way!" |
| **Photo Share** | Camera icon to send photo of substitute item |
| **Call Button** | Phone icon — initiates masked call |

### Screen 5.5: Earnings Dashboard

| Element | Specification |
|---------|---------------|
| **Header** | "My Earnings" |
| **Balance Card** | Available Balance (large, prominent) / Pending Balance |
| **Withdraw Button** | "Withdraw to M-Pesa" — opens amount input. Validates min (2,000) and max (200,000/day). |
| **Today** | Today's earnings with per-order breakdown |
| **This Week** | Weekly total + daily bar chart |
| **This Month** | Monthly total + comparison to previous month |
| **Order History** | List of completed orders with earnings per order |

---

## 6. Admin Dashboard — Screen Map

```
┌──────────────────────────────────────────────────────────┐
│                 ADMIN DASHBOARD (WEB)                    │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  SIDEBAR                    MAIN CONTENT AREA            │
│  ┌──────────────┐          ┌───────────────────────────┐ │
│  │ 📊 Overview   │          │  Metrics Cards Row        │ │
│  │ 📋 Shoppers   │          │  ┌─────┐ ┌─────┐ ┌────┐ │ │
│  │ 📦 Orders     │          │  │GMV  │ │Active│ │Fulf│ │ │
│  │ ⚖️ Disputes   │          │  │TZS X│ │Shprs│ │Rate│ │ │
│  │ 🚨 Fraud      │          │  └─────┘ └─────┘ └────┘ │ │
│  │ 🌍 Zones      │          ├───────────────────────────┤ │
│  │ 📈 Reports    │          │  Live Orders Map         │ │
│  │ ⚙️ Settings   │          │  (Shopper pins on map)   │ │
│  └──────────────┘          ├───────────────────────────┤ │
│                             │  Recent Disputes Table   │ │
│                             │  Pending Applications    │ │
│                             └───────────────────────────┘ │
└──────────────────────────────────────────────────────────┘
```

---

## 7. Admin Dashboard — Screen Specifications

### Screen 7.1: Overview Dashboard

| Component | Specification |
|-----------|---------------|
| **Metrics Cards** | GMV (Today / Week / Month), Active Shoppers, Order Fulfillment Rate, Avg Assignment Time, Dispute Rate. Each card shows value + trend arrow. |
| **Live Orders Map** | Map of the service area with: active order pins, shopper locations, zone boundaries colour-coded by health (green/yellow/red based on supply/demand ratio). |
| **Recent Activity Feed** | Scrollable list of recent events: orders accepted, disputes opened, shoppers approved, fraud alerts. |
| **Quick Actions** | "Pending Reviews" badge count, "Open Disputes" badge count. |

### Screen 7.2: Shopper Management

| Component | Specification |
|-----------|---------------|
| **Filter Tabs** | All / Pending Verification / Active / Suspended / Deactivated |
| **Shopper Table** | Columns: Name, Phone, Rating, Orders, Acceptance %, Status. Sortable. Searchable. |
| **Detail View** | Tap a shopper to open: full profile, documents (ID, PCC, transport), order history, earnings history, rating breakdown, dispute history, status timeline. |
| **Actions** | Approve/Reject (pending), Suspend/Warn (active), Reinstate (suspended), Deactivate. Each requires reason + confirmation. |

### Screen 7.3: Order Management

| Component | Specification |
|-----------|---------------|
| **Search** | Search by Order ID, Customer phone, Shopper phone |
| **Filter Tabs** | All / Active / Completed / Cancelled / Disputed |
| **Order Table** | Columns: ID, Customer, Shopper, Items, Total, Status, Created. Sortable. |
| **Detail View** | Full order timeline: state transitions with timestamps, item list with statuses, receipt photos, chat logs, GPS route trace, payment status. |

### Screen 7.4: Dispute Queue (Most Important Admin Screen)

| Component | Specification |
|-----------|---------------|
| **Queue Display** | Prioritised list: Urgent (behavioural, > 100k) → Standard → Automated (resolved). Each item shows: dispute ID, order ID, type, value, assigned agent, age. |
| **Detail View** | Full dispute context: order details, customer claim, shopper response, evidence (photos, chat, receipt, GPS). Decision panel with: Approve (full), Approve (partial — enter amount), Reject (with reason), Escalate (to Ops Admin). Goodwill field (discretionary, shows cap). |
| **Decision Recording** | Required: decision rationale (free text). Optional: root cause category. Auto-logged with admin ID and timestamp. |

### Screen 7.5: Fraud Alerts

| Component | Specification |
|-----------|---------------|
| **Alert Table** | Columns: Alert ID, Type, User, Severity (Low/Med/High/Critical), Timestamp, Status. Colour-coded by severity. |
| **Detail View** | Alert evidence: triggering rule, user history, related orders, device data, IP data, GPS data. Actions: Dismiss (false alarm), Monitor (flag account for 7-day observation), Suspend (temporary), Ban (permanent). |

### Screen 7.6: Zone Management

| Component | Specification |
|-----------|---------------|
| **Zone List** | All zones with: name, status, shopper count, MDM, operating hours. |
| **Edit Zone** | Map-based boundary editor. Fields: name, status toggle, max assignment radius (km), operating hours (start/end), base delivery fee, per-km delivery fee, supply/demand threshold. |

### Screen 7.7: Reports

| Component | Specification |
|-----------|---------------|
| **Report Types** | Daily Ops Summary, Weekly Performance, Monthly Financial, Shopper Earnings, Tax Summary, Incident Report |
| **Format** | CSV / PDF export. Preview on screen. Date range picker. |
| **Scheduled Reports** | Configure recurring email delivery of reports. |

---

## 8. Shared Components

### 8.1 Navigation Patterns

| Pattern | Customer App | Shopper App | Admin Dashboard |
|---------|-------------|-------------|-----------------|
| **Primary Navigation** | Bottom tab bar (4 tabs) | Bottom tab bar (3 tabs) | Left sidebar menu |
| **Back Navigation** | Back arrow (top-left) + Android hardware back | Back arrow + Android hardware back | Breadcrumb navigation |
| **Modal / Overlay** | Offer countdown (shopper) — full-screen overlay | — | Confirmation dialogs |

### 8.2 Status Badges

| Status | Colour | Icon |
|--------|--------|------|
| Active / Online | Green (#2ECC71) | ● |
| Inactive / Offline | Grey (#95A5A6) | ● |
| Pending / Processing | Amber (#F39C12) | ◌ |
| Suspended / Blocked | Red (#E74C3C) | ● |
| Completed | Green (#2ECC71) | ✓ |
| Cancelled | Red (#E74C3C) | ✕ |
| Disputed | Amber (#F39C12) | ⚠ |
| Delayed | Red (#E74C3C) | ⏰ |

### 8.3 Confirmation Dialogs

| Action | Dialog Content |
|--------|----------------|
| Place Order | "Place this order for TZS X,XXX? A hold of X,XXX will be placed on your M-Pesa." [Cancel] [Place Order] |
| Cancel Order | "Cancel this order? Cancellation fee: TZS X,XXX." [Keep Order] [Cancel Anyway] |
| Go Offline | "Go offline? You will stop receiving offers." [Stay Online] [Go Offline] |
| Withdraw Funds | "Withdraw TZS X,XXX to your M-Pesa account ending XXX?" [Cancel] [Withdraw] |
| Delete Account | "Delete your account? This cannot be undone." [Cancel] [Delete] |

---

## 9. Error States

| Scenario | User Message | Action |
|----------|-------------|--------|
| Network unavailable | "No internet connection. Your order status is still available offline." | Retry button. Show cached data. |
| Payment failed | "Payment failed. We'll try again. If it continues to fail, you can switch to Cash on Delivery." | Auto-retry (2x). Switch to COD option. Cancel option. |
| No shoppers available | "No shoppers available right now. Try expanding the delivery area or increasing the delivery fee." | Options: expand radius, increase fee, cancel. |
| GPS unavailable | "Location unavailable. Please enable GPS for the best experience." | Settings button. Manual location entry fallback. |
| OTP failed | "We couldn't send a verification code. Try again or request a voice call." | Retry button. Voice call fallback. |
| App update required | "Please update your app to continue using Urban Shopper." | Update button → Play Store. |
| Session expired | "Session expired. Please log in again." | Login redirect. |

---

## 10. Loading States

| Scenario | Loading Indicator | Timeout Behaviour |
|----------|------------------|-------------------|
| Order submission | Full-screen spinner with "Placing your order..." | After 15s: "Taking longer than expected. We'll notify you when it's ready." Allow navigation away. |
| Receipt upload | Upload progress bar per photo. "Uploading receipt X of Y..." | After 30s per photo: "Upload slow. You can continue while it uploads in the background." |
| Payment processing | Spinner with "Processing payment..." | After 20s: "Payment is taking longer than usual. Your order will still be processed." |
| Data loading | Skeleton screens (not spinners) for list views | After 10s: show cached data with "Could not refresh." |
| Image loading | Low-resolution placeholder with blur-up | Show error state if > 10s |

---

## 11. Empty States

| Screen | Empty State Message | Action |
|--------|---------------------|--------|
| Order History | "No orders yet. Place your first order!" | [New Order] button |
| Shopper Earnings | "Start accepting orders to see your earnings." | [Go Online] button |
| Saved Addresses | "No saved addresses. Save one for faster checkout." | [Add Address] button |
| Notification List | "No notifications yet." | — |
| Dispute Queue (Admin) | "No open disputes. Everything looks good!" | — |
| Shopper List (Admin) | "No shoppers registered in this zone yet." | — |

---

## 12. Push Notification Templates

| Event | Customer Notification | Shopper Notification |
|-------|----------------------|---------------------|
| Order Accepted | "Juma has accepted your order and is heading to Kariakoo Market!" | — |
| Shopping Started | "Juma is shopping your items at Kariakoo Market." | — |
| Substitution Request | "Juma found a substitute for 'Green Bananas'. Tap to approve." | — |
| Shopping Complete | "Shopping complete! Final cost: TZS X,XXX. Juma is on their way!" | — |
| Delivery ETA Updated | "Your delivery ETA has been updated. New ETA: ~XX min" | — |
| Shopper Arrived | "Your shopper has arrived!" | — |
| Order Delivered | "Your order has been delivered! Rate your shopper." | "Order delivered! TZS X,XXX added to your earnings." |
| Payment Received | "Receipt: Payment of TZS X,XXX confirmed." | "Payout of TZS X,XXX added to your available balance." |
| Offer Received | — | "New order: 12 items from Kariakoo Market. Estimated pay: TZS X,XXX" |
| Order Cancelled | "Order cancelled. Refund of TZS X,XXX processed." | "Order cancelled. Compensation of TZS X,XXX added." |
| New Shopper Application | — | "Shopper application submitted. We'll review it within 48 hours." |
| Application Approved | — | "Application approved! Complete your onboarding training to start." |
| Quality Warning | — | "Your rating has dropped to X.X. Complete quality training to improve." |
| Emergency Alert | "Your shopper has reported an emergency. Your order has been cancelled with no charge." | — |

---

*This document is Phase 9 of the Urban Shopper Platform specification. It defines the UX for all three applications (Customer, Shopper, Admin) and is intended for use by UI/UX designers and frontend developers.*
