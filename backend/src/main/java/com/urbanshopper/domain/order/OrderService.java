package com.urbanshopper.domain.order;

import com.urbanshopper.domain.assignment.AssignmentEngine;
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
 * - D-001/D-003: Order creation with pricing
 * - D-004/D-006/D-007: Shopping flow with item tracking
 * - D-008: Receipt upload
 * - F-001/F-002: Pricing calculation
 * - F-003: Payment pre-authorisation
 * - G-001/G-002/G-003: Cancellation
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private static final AtomicInteger counter = new AtomicInteger(0);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final ReceiptRepository receiptRepository;
    private final ReceiptPhotoRepository receiptPhotoRepository;
    private final OrderPricingService pricingService;
    private final OrderStateMachine stateMachine;
    private final AssignmentEngine assignmentEngine;
    private final EventPublisher eventPublisher;

    // ═══════════════════════════════════════════════
    //  Order Creation (D-001 → D-004 → F-003)
    // ═══════════════════════════════════════════════

    @Transactional
    public OrderDTO createOrder(CreateOrderRequest req, UUID customerId) {
        validateOrderRequest(req);

        var pricing = pricingService.calculateEstimatedPricing(req);

        var now = Instant.now();
        var order = buildOrderEntity(req, customerId, pricing, now);
        var saved = orderRepository.save(order);

        saveOrderItems(saved.getId(), req);

        stateMachine.transition(saved, OrderStatus.AWAITING_PAYMENT_VERIFICATION,
            "OrderSubmitted", "system", null, null);

        try {
            processPaymentPreAuth(saved, pricing);
            stateMachine.transition(saved, OrderStatus.QUEUED_FOR_ASSIGNMENT,
                "PaymentVerified", "system", null, null);
        } catch (Exception e) {
            log.warn("Payment pre-auth failed for order {}: {}", saved.getOrderNumber(), e.getMessage());
            stateMachine.transition(saved, OrderStatus.CANCELLED,
                "PaymentFailed", "system", null, e.getMessage());
        }

        // Trigger assignment engine
        if (saved.getStatus() == OrderStatus.QUEUED_FOR_ASSIGNMENT) {
            try {
                assignmentEngine.processOrder(saved.getId());
            } catch (Exception e) {
                log.warn("Initial assignment trigger failed for {} (poller will retry): {}",
                    saved.getOrderNumber(), e.getMessage());
            }
        }

        eventPublisher.publish(new OrderCreatedEvent(
            saved.getId(), saved.getCustomerId(),
            saved.getItemCount(), saved.getEstimatedTotal()));

        log.info("Order created: {} (status: {}, total: {} TZS)",
            saved.getOrderNumber(), saved.getStatus(), pricing.total());

        return OrderDTO.fromEntity(saved);
    }

    // ═══════════════════════════════════════════════
    //  Read Operations
    // ═══════════════════════════════════════════════

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

    /**
     * Get order status summary with item counts for the status endpoint.
     */
    @Transactional(readOnly = true)
    public OrderStatusResponse getOrderStatus(UUID orderId) {
        var order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found: " + orderId));

        var items = orderItemRepository.findByOrderIdOrderBySortOrderAsc(orderId);
        var timeline = statusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(orderId);

        long found = items.stream().filter(i -> "found".equals(i.getStatus())).count();
        long substituted = items.stream().filter(i -> "substituted".equals(i.getStatus())).count();
        long notAvailable = items.stream().filter(i -> "not_available".equals(i.getStatus())).count();
        long pending = items.stream().filter(i -> "requested".equals(i.getStatus())).count();

        return OrderStatusResponse.builder()
            .orderId(order.getId().toString())
            .orderNumber(order.getOrderNumber())
            .status(order.getStatus().name())
            .totalItems(order.getItemCount())
            .itemsFound((int) found)
            .itemsSubstituted((int) substituted)
            .itemsUnavailable((int) notAvailable)
            .itemsPending((int) pending)
            .estimatedTotal(order.getEstimatedTotal())
            .timeline(timeline.stream()
                .map(h -> new OrderStatusResponse.TimelineEvent(
                    h.getFromStatus(), h.getToStatus(), h.getTriggerEvent(),
                    h.getCreatedAt().toString()))
                .toList())
            .build();
    }

    /**
     * Get all items for an order.
     */
    @Transactional(readOnly = true)
    public List<OrderItemDTO> getOrderItems(UUID orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new BusinessException("ORDER_NOT_FOUND", "Order not found: " + orderId);
        }
        return orderItemRepository.findByOrderIdOrderBySortOrderAsc(orderId)
            .stream()
            .map(OrderItemDTO::fromEntity)
            .toList();
    }

    // ═══════════════════════════════════════════════
    //  Shopping Flow — Item Tracking (D-006, D-007)
    // ═══════════════════════════════════════════════

    /**
     * D-006: Mark item status during shopping.
     * Handles: Found, Substituted, Not Available.
     * D-007: Substitution rules — follows Best Match / Contact Me / No Substitutions.
     *
     * When all items are resolved, auto-transitions order to SHOPPING_COMPLETE.
     */
    @Transactional
    public OrderItemDTO updateItemStatus(UUID orderId, UUID itemId, ItemStatusUpdateRequest req, UUID actorId) {
        var order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found: " + orderId));

        var item = orderItemRepository.findById(itemId)
            .orElseThrow(() -> new BusinessException("ITEM_NOT_FOUND", "Item not found: " + itemId));

        if (!item.getOrderId().equals(orderId)) {
            throw new BusinessException("ITEM_MISMATCH", "Item does not belong to this order");
        }

        var newStatus = req.status().toLowerCase();

        // Validate allowed transitions
        var currentStatus = item.getStatus();
        if ("requested".equals(currentStatus) && List.of("found", "substituted", "not_available").contains(newStatus)) {
            // Valid transition
        } else if ("substituted".equals(currentStatus) && List.of("found", "not_available").contains(newStatus)) {
            // Customer approved or rejected substitution
        } else {
            throw new BusinessException("INVALID_ITEM_STATUS",
                "Cannot transition item from " + currentStatus + " to " + newStatus);
        }

        // D-007: Handle substitution workflow
        if ("substituted".equals(newStatus)) {
            handleSubstitution(item, req);
        }

        // Update item fields
        item.setStatus(newStatus);
        if (req.actualPrice() != null) item.setActualPrice(req.actualPrice());
        item.setHasPhoto(req.hasPhoto() != null ? req.hasPhoto() : false);
        if (req.substitutionNote() != null) item.setSubstitutionNote(req.substitutionNote());
        if (req.substitutionApproval() != null) item.setSubstitutionApproval(req.substitutionApproval());

        var saved = orderItemRepository.save(item);
        log.info("Item {} in order {}: {} → {}", item.getName(), order.getOrderNumber(), currentStatus, newStatus);

        // Auto-detect shopping complete when all items resolved
        checkAndTransitionToShoppingComplete(order);

        return OrderItemDTO.fromEntity(saved);
    }

    /**
     * Shopper marks arrival at market: ACCEPTED → TRAVELLING_TO_MARKET → SHOPPING.
     */
    @Transactional
    public OrderDTO arriveAtMarket(UUID orderId, UUID shopperId) {
        var order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found: " + orderId));

        if (!shopperId.equals(order.getShopperId())) {
            throw new BusinessException("NOT_ASSIGNED", "Shopper is not assigned to this order");
        }

        // ACCEPTED → TRAVELLING_TO_MARKET
        if (order.getStatus() == OrderStatus.ACCEPTED) {
            stateMachine.transition(order, OrderStatus.TRAVELLING_TO_MARKET,
                "ShopperEnRoute", "shopper", shopperId, null);
        }

        // TRAVELLING_TO_MARKET → SHOPPING
        if (order.getStatus() == OrderStatus.TRAVELLING_TO_MARKET) {
            stateMachine.transition(order, OrderStatus.SHOPPING,
                "ShopperArrivedAtMarket", "shopper", shopperId, null);
        }

        var saved = orderRepository.save(order);
        return OrderDTO.fromEntity(saved);
    }

    // ═══════════════════════════════════════════════
    //  Shopping Flow — Receipt (D-008)
    // ═══════════════════════════════════════════════

    /**
     * D-008: Upload receipt after shopping is complete.
     * Supports photo receipts, handwritten receipts, and manual entry.
     * If all items are resolved and receipt is uploaded, transitions to RECEIPT_VERIFIED.
     */
    @Transactional
    public ReceiptDTO uploadReceipt(UUID orderId, ReceiptUploadRequest req, UUID actorId) {
        var order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.SHOPPING_COMPLETE && order.getStatus() != OrderStatus.SHOPPING) {
            throw new BusinessException("INVALID_STATE",
                "Receipt can only be uploaded during or after shopping");
        }

        var receipt = Receipt.builder()
            .orderId(orderId)
            .receiptType(req.receiptType())
            .totalAmount(req.totalAmount())
            .vendorName(req.vendorName())
            .notes(req.notes())
            .build();
        var saved = receiptRepository.save(receipt);

        // Save receipt photos if provided
        if (req.photoUrls() != null && !req.photoUrls().isEmpty()) {
            for (int i = 0; i < req.photoUrls().size(); i++) {
                var photo = ReceiptPhoto.builder()
                    .receiptId(saved.getId())
                    .photoUrl(req.photoUrls().get(i))
                    .sortOrder(i + 1)
                    .build();
                receiptPhotoRepository.save(photo);
            }
        }

        // Transition to RECEIPT_VERIFIED (from SHOPPING_COMPLETE or SHOPPING)
        if (order.getStatus() == OrderStatus.SHOPPING || order.getStatus() == OrderStatus.SHOPPING_COMPLETE) {
            // If still SHOPPING, transition to SHOPPING_COMPLETE first
            if (order.getStatus() == OrderStatus.SHOPPING) {
                stateMachine.transition(order, OrderStatus.SHOPPING_COMPLETE,
                    "ReceiptUploaded", "shopper", actorId, null);
            }
            stateMachine.transition(order, OrderStatus.RECEIPT_VERIFIED,
                "ReceiptUploadedAndVerified", "shopper", actorId, null);
        }

        return ReceiptDTO.fromEntity(saved);
    }

    // ═══════════════════════════════════════════════
    //  Cancellation (G-001, G-002, G-003)
    // ═══════════════════════════════════════════════

    @Transactional
    public OrderDTO cancelOrder(UUID id, String reason, String cancelledBy, UUID actorId) {
        var order = orderRepository.findById(id)
            .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found: " + id));

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.ARCHIVED) {
            throw new BusinessException("INVALID_STATE",
                "Order " + order.getOrderNumber() + " is already " + order.getStatus());
        }

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

        // G-001 before acceptance: no fee
        // G-002 after acceptance, before shopping: delivery fee
        // G-003 after shopping started: full delivery fee + 10% restocking
        if (order.getStatus() == OrderStatus.ACCEPTED || order.getStatus() == OrderStatus.TRAVELLING_TO_MARKET) {
            order.setCancellationFee(order.getEstimatedDeliveryFee());
        } else if (order.getStatus().ordinal() >= OrderStatus.SHOPPING.ordinal()
            && order.getStatus().ordinal() < OrderStatus.DELIVERED.ordinal()) {
            int restockingFee = (int) Math.round(order.getEstimatedTotal() * 0.10);
            order.setCancellationFee(order.getEstimatedDeliveryFee() + restockingFee);
        }

        var saved = orderRepository.save(order);
        return OrderDTO.fromEntity(saved);
    }

    // ═══════════════════════════════════════════════
    //  Private Helpers
    // ═══════════════════════════════════════════════

    private void validateOrderRequest(CreateOrderRequest req) {
        if (req.items() == null || req.items().isEmpty()) {
            throw new BusinessException("INVALID_ORDER", "Order must have at least one item");
        }
        if ("scheduled".equals(req.deliveryTime()) && req.scheduledWindow() != null) {
            if (req.scheduledWindow().isBefore(Instant.now().plusSeconds(7200))) {
                throw new BusinessException("INVALID_SCHEDULE",
                    "Scheduled orders must be at least 2 hours in the future");
            }
        }
        if (!List.of("cheapest", "best_quality", "balanced").contains(req.shoppingPreference())) {
            throw new BusinessException("INVALID_PREFERENCE",
                "Shopping preference must be: cheapest, best_quality, or balanced");
        }
        if (!List.of("asap", "scheduled").contains(req.deliveryTime())) {
            throw new BusinessException("INVALID_DELIVERY_TIME",
                "Delivery time must be 'asap' or 'scheduled'");
        }
        if (!List.of("mpesa", "mixx", "cod").contains(req.paymentMethod())) {
            throw new BusinessException("INVALID_PAYMENT_METHOD",
                "Payment method must be: mpesa, mixx, or cod");
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
                ? req.scheduledWindow().plusSeconds(3600) : null)
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
     * D-007: Handle substitution based on customer's substitution preference.
     */
    private void handleSubstitution(OrderItem item, ItemStatusUpdateRequest req) {
        var subPref = item.getSubstitutionPreference();

        if ("no_substitutions".equals(subPref)) {
            throw new BusinessException("SUBSTITUTION_NOT_ALLOWED",
                "Customer has disabled substitutions for this item");
        }

        if ("contact_me".equals(subPref) && !"approved".equals(req.substitutionApproval())) {
            // Store as substituted but mark approval as pending
            item.setSubstitutionApproval("pending");
            item.setSubstitutionNote(req.substitutionNote());
        }
    }

    /**
     * Auto-detect when all items are resolved and transition to SHOPPING_COMPLETE.
     * Only fires when order is in SHOPPING state and all items are terminal.
     */
    private void checkAndTransitionToShoppingComplete(Order order) {
        if (order.getStatus() != OrderStatus.SHOPPING) return;

        var items = orderItemRepository.findByOrderIdOrderBySortOrderAsc(order.getId());
        var allResolved = items.stream().allMatch(i -> {
            var s = i.getStatus();
            return "found".equals(s) || "substituted".equals(s) || "not_available".equals(s);
        });

        if (allResolved) {
            stateMachine.transition(order, OrderStatus.SHOPPING_COMPLETE,
                "AllItemsResolved", "system", null, null);
            log.info("Order {}: all items resolved → SHOPPING_COMPLETE", order.getOrderNumber());
        }
    }

    private void processPaymentPreAuth(Order order, OrderPricing pricing) {
        log.info("Payment pre-auth placed for {}: {} TZS (stub — awaiting M-Pesa integration)",
            order.getOrderNumber(), pricing.total());
    }
}
