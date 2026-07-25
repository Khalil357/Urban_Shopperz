package com.urbanshopper.domain.order;

import com.urbanshopper.domain.order.events.OrderCreatedEvent;
import com.urbanshopper.shared.events.EventPublisher;
import com.urbanshopper.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Order Service — manages the complete order lifecycle.
 *
 * Implements business rules from 03-business-rules.md:
 * - D-001: Order creation with minimum requirements
 * - D-003: Pricing visibility with variance control
 * - D-004: State machine lifecycle with ordered transitions
 * - F-001/F-002: Pricing calculation
 * - F-003: Payment pre-authorisation flow
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private static final AtomicInteger counter = new AtomicInteger(0);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderPricingService pricingService;
    private final OrderStateMachine stateMachine;
    private final EventPublisher eventPublisher;

    // ──────────────────────────────────────────────
    //  Order Creation (D-001 → D-004 → F-003)
    // ──────────────────────────────────────────────

    /**
     * Complete order creation flow:
     *
     * 1. Validate business rules (D-001)
     * 2. Calculate estimated pricing (F-001, F-002)
     * 3. Build and persist Order entity
     * 4. Build and persist OrderItems
     * 5. Transition CREATED → AWAITING_PAYMENT_VERIFICATION
     * 6. Execute payment pre-auth (stubbed — real M-Pesa integration in Phase 1)
     * 7. On success: transition → QUEUED_FOR_ASSIGNMENT
     *    On failure: transition → CANCELLED
     * 8. Publish OrderCreatedEvent
     * 9. Return OrderDTO
     */
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest req, UUID customerId) {
        // ── Step 1: Validate business rules (D-001) ──
        validateOrderRequest(req);

        // ── Step 2: Calculate estimated pricing (F-001, F-002) ──
        var pricing = pricingService.calculateEstimatedPricing(req);

        // ── Step 3: Build and save Order entity ──
        var now = Instant.now();
        var order = buildOrderEntity(req, customerId, pricing, now);
        var saved = orderRepository.save(order);

        // ── Step 4: Save OrderItems ──
        saveOrderItems(saved.getId(), req);

        // ── Step 5: Initial state transition ──
        // CREATED → AWAITING_PAYMENT_VERIFICATION
        stateMachine.transition(saved, OrderStatus.AWAITING_PAYMENT_VERIFICATION,
            "OrderSubmitted", "system", null, null);

        // ── Step 6 & 7: Payment pre-auth (F-003) ──
        // Placeholder: simulate successful payment verification
        // TODO: Replace with real M-Pesa/Mixx integration (Phase 1 — Week 11-12)
        try {
            processPaymentPreAuth(saved, pricing);
            stateMachine.transition(saved, OrderStatus.QUEUED_FOR_ASSIGNMENT,
                "PaymentVerified", "system", null, null);
        } catch (Exception e) {
            log.warn("Payment pre-auth failed for order {}: {}", saved.getOrderNumber(), e.getMessage());
            stateMachine.transition(saved, OrderStatus.CANCELLED,
                "PaymentFailed", "system", null, e.getMessage());
        }

        // ── Step 8: Publish domain event ──
        eventPublisher.publish(new OrderCreatedEvent(
            saved.getId(), saved.getCustomerId(),
            saved.getItemCount(), saved.getEstimatedTotal()));

        log.info("Order created: {} (status: {}, total: {} TZS)",
            saved.getOrderNumber(), saved.getStatus(), pricing.total());

        // ── Step 9: Return DTO ──
        return OrderDTO.fromEntity(saved);
    }

    // ──────────────────────────────────────────────
    //  Read Operations
    // ──────────────────────────────────────────────

    @Transactional(readOnly = true)
    public OrderDTO getOrder(UUID id) {
        return orderRepository.findById(id)
            .map(OrderDTO::fromEntity)
            .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getCustomerOrders(UUID customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
            .stream()
            .map(OrderDTO::fromEntity)
            .toList();
    }

    // ──────────────────────────────────────────────
    //  Cancellation (G-001, G-002, G-003)
    // ──────────────────────────────────────────────

    /**
     * Cancel an order with full audit trail.
     * Handles:
     * - G-001: Customer cancellation before acceptance
     * - G-002: Customer cancellation after acceptance, before shopping
     * - G-003: Customer cancellation after shopping started
     * - G-004: Shopper-initiated cancellation
     * - G-005: Platform-initiated cancellation
     */
    @Transactional
    public OrderDTO cancelOrder(UUID id, String reason, String cancelledBy, UUID actorId) {
        var order = orderRepository.findById(id)
            .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found: " + id));

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.ARCHIVED) {
            throw new BusinessException("INVALID_STATE",
                "Order " + order.getOrderNumber() + " is already " + order.getStatus());
        }

        // Determine trigger event based on who is cancelling
        String triggerEvent = switch (cancelledBy) {
            case "customer" -> "CustomerCancellation";
            case "shopper" -> "ShopperCancellation";
            case "admin" -> "AdminCancellation";
            case "system" -> "PlatformInitiatedCancellation";
            default -> "CancellationRequested";
        };

        stateMachine.transition(order, OrderStatus.CANCELLED, triggerEvent, cancelledBy, actorId, reason);

        order.setCancellationReason(reason);
        order.setCancelledBy(cancelledBy);
        order.setCancelledAt(Instant.now());

        // Calculate cancellation fee per G-002/G-003 based on current status
        // G-001 (before acceptance): no fee
        // G-002 (after acceptance, before shopping): actual progress compensation
        // G-003 (after shopping started): full delivery fee + 10% restocking
        if (order.getStatus() == OrderStatus.ACCEPTED || order.getStatus() == OrderStatus.TRAVELLING_TO_MARKET) {
            order.setCancellationFee(order.getEstimatedDeliveryFee()); // G-002: delivery fee as cancellation fee
        } else if (order.getStatus().ordinal() >= OrderStatus.SHOPPING.ordinal()
            && order.getStatus().ordinal() < OrderStatus.DELIVERED.ordinal()) {
            // G-003: full delivery fee + 10% restocking
            int restockingFee = (int) Math.round(order.getEstimatedTotal() * 0.10);
            order.setCancellationFee(order.getEstimatedDeliveryFee() + restockingFee);
        }

        var saved = orderRepository.save(order);
        return OrderDTO.fromEntity(saved);
    }

    // ──────────────────────────────────────────────
    //  Private Helpers
    // ──────────────────────────────────────────────

    private void validateOrderRequest(CreateOrderRequest req) {
        // D-001: Minimum requirements validation
        if (req.items() == null || req.items().isEmpty()) {
            throw new BusinessException("INVALID_ORDER", "Order must have at least one item");
        }

        // D-001: Scheduled orders require a time at least 2 hours in the future
        if ("scheduled".equals(req.deliveryTime()) && req.scheduledWindow() != null) {
            if (req.scheduledWindow().isBefore(Instant.now().plusSeconds(7200))) {
                throw new BusinessException("INVALID_SCHEDULE",
                    "Scheduled orders must be at least 2 hours in the future");
            }
        }

        // Validate shopping preference (D-001)
        var validPreferences = List.of("cheapest", "best_quality", "balanced");
        if (!validPreferences.contains(req.shoppingPreference())) {
            throw new BusinessException("INVALID_PREFERENCE",
                "Shopping preference must be one of: cheapest, best_quality, balanced");
        }

        // Validate delivery preference
        if (!List.of("asap", "scheduled").contains(req.deliveryTime())) {
            throw new BusinessException("INVALID_DELIVERY_TIME",
                "Delivery time must be 'asap' or 'scheduled'");
        }

        // Validate payment method
        if (!List.of("mpesa", "mixx", "cod").contains(req.paymentMethod())) {
            throw new BusinessException("INVALID_PAYMENT_METHOD",
                "Payment method must be one of: mpesa, mixx, cod");
        }
    }

    private Order buildOrderEntity(CreateOrderRequest req, UUID customerId, OrderPricing pricing, Instant now) {
        var orderNum = "URB-" + now.toString().substring(0, 10).replace("-", "")
            + "-" + String.format("%04d", counter.incrementAndGet());

        var delivery = req.deliveryLocation();

        return Order.builder()
            .orderNumber(orderNum)
            .customerId(customerId)
            .status(OrderStatus.CREATED)
            .zoneId(req.zoneId())
            .marketId(req.marketId())
            .deliveryLat(delivery.latitude())
            .deliveryLng(delivery.longitude())
            .deliveryAddressText(delivery.addressText())
            .deliveryLandmark(delivery.landmark())
            .shoppingPreference(req.shoppingPreference())
            .deliveryPreference(req.deliveryTime())
            .scheduledWindowStart("scheduled".equals(req.deliveryTime()) ? req.scheduledWindow() : null)
            .scheduledWindowEnd("scheduled".equals(req.deliveryTime()) && req.scheduledWindow() != null
                ? req.scheduledWindow().plusSeconds(3600) : null) // default 1-hour delivery window
            .paymentMethod(req.paymentMethod())
            .estimatedItemCost(pricing.estimatedItemCost())
            .estimatedServiceFee(pricing.serviceFee())
            .estimatedDeliveryFee(pricing.deliveryFee())
            .estimatedTotal(pricing.total())
            .itemCount(req.items().size())
            .build();
    }

    private void saveOrderItems(UUID orderId, CreateOrderRequest req) {
        var items = req.items();
        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);
            var orderItem = OrderItem.builder()
                .orderId(orderId)
                .name(item.name())
                .quantity(item.quantity())
                .unit(item.unit())
                .preferredBrand(item.preferredBrand())
                .maxPrice(item.maxPrice())
                .notes(item.notes())
                .substitutionPreference(req.substitutionDefault())
                .sortOrder(i + 1)
                .build();
            orderItemRepository.save(orderItem);
        }
    }

    /**
     * F-003: Payment pre-authorisation.
     *
     * MVP stub — simulates a pre-auth hold on the customer's mobile money.
     * TODO: Implement real M-Pesa/Mixx integration in Phase 1 (Week 11-12).
     *
     * The real implementation will:
     * 1. Call M-Pesa API to place a hold for pricing.total()
     * 2. Store the hold reference on the payment record
     * 3. On failure, retry up to 2 times within 5 minutes (F-011)
     * 4. If all retries fail, cancel the order
     */
    private void processPaymentPreAuth(Order order, OrderPricing pricing) {
        log.info("Payment pre-auth placed for {}: {} TZS (stub — awaiting M-Pesa integration)",
            order.getOrderNumber(), pricing.total());
        // Stub: always succeeds in MVP
        // Real implementation will throw on payment failure
    }
}
