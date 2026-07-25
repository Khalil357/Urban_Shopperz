package com.urbanshopper.domain.order;

import com.urbanshopper.domain.order.events.OrderCreatedEvent;
import com.urbanshopper.shared.events.EventPublisher;
import com.urbanshopper.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;
    private static final AtomicInteger counter = new AtomicInteger(0);

    @Transactional
    public OrderDTO createOrder(CreateOrderRequest req) {
        var now = Instant.now();
        var orderNum = "URB-" + now.toString().substring(0, 10).replace("-", "")
            + "-" + String.format("%04d", counter.incrementAndGet());

        var itemCount = req.items().size();

        // Calculate estimated totals from item requests where maxPrice is provided
        var estimatedItemCost = req.items().stream()
            .filter(i -> i.maxPrice() != null)
            .mapToInt(i -> i.maxPrice() * i.quantity())
            .sum();

        var order = Order.builder()
            .orderNumber(orderNum)
            .customerId(req.customerId())
            .zoneId(req.zoneId())
            .marketId(req.marketId())
            .shoppingPreference(req.shoppingPreference())
            .deliveryPreference(req.deliveryPreference())
            .scheduledWindowStart(req.scheduledWindowStart())
            .scheduledWindowEnd(req.scheduledWindowEnd())
            .paymentMethod(req.paymentMethod())
            .deliveryLat(req.deliveryLat())
            .deliveryLng(req.deliveryLng())
            .deliveryAddressText(req.deliveryAddressText())
            .deliveryLandmark(req.deliveryLandmark())
            .estimatedItemCost(estimatedItemCost)
            .estimatedTotal(estimatedItemCost)
            .itemCount(itemCount)
            .build();

        var saved = orderRepository.save(order);

        eventPublisher.publish(new OrderCreatedEvent(
            saved.getId(), saved.getCustomerId(),
            saved.getItemCount(), saved.getEstimatedTotal()));

        return OrderDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrder(UUID id) {
        return orderRepository.findById(id)
            .map(OrderDTO::fromEntity)
            .orElseThrow(() -> new BusinessException("NOT_FOUND", "Order not found"));
    }

    @Transactional
    public OrderDTO cancelOrder(UUID id, String reason, String cancelledBy, UUID actorId) {
        var order = orderRepository.findById(id)
            .orElseThrow(() -> new BusinessException("NOT_FOUND", "Order not found"));
        order.transitionTo(OrderStatus.CANCELLED);
        order.setCancellationReason(reason);
        order.setCancelledBy(cancelledBy);
        order.setCancelledAt(Instant.now());
        var saved = orderRepository.save(order);
        return OrderDTO.fromEntity(saved);
    }
}
