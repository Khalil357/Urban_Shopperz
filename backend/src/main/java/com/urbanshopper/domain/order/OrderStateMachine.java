package com.urbanshopper.domain.order;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Order state machine engine.
 * Handles validated transitions with history persistence per D-004 and SM-10.
 *
 * Every transition is logged with: timestamp, from-state, to-state, trigger event, actor, and reason.
 * Invalid transitions are rejected (canTransitionTo guard).
 */
@Component
@RequiredArgsConstructor
public class OrderStateMachine {

    private static final Logger log = LoggerFactory.getLogger(OrderStateMachine.class);
    private final OrderStatusHistoryRepository historyRepository;

    /**
     * Execute a state machine transition on the given order.
     *
     * @param order       the order entity (must be managed or newly saved)
     * @param target      the target status
     * @param triggerEvent the event that triggered this transition (e.g. "OrderSubmitted", "PaymentVerified")
     * @param actorType   who/what performed the transition ("customer", "shopper", "system", "admin")
     * @param actorId     UUID of the actor, if applicable
     * @param reason      human-readable reason, if applicable (e.g. cancellation reason)
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void transition(Order order, OrderStatus target, String triggerEvent,
                           String actorType, UUID actorId, String reason) {
        var fromStatus = order.getStatus();

        // Validate — throws IllegalStateException if invalid
        order.transitionTo(target);

        // Persist history record (SM-10)
        var history = OrderStatusHistory.builder()
            .orderId(order.getId())
            .fromStatus(fromStatus.name())
            .toStatus(target.name())
            .triggerEvent(triggerEvent)
            .actorType(actorType)
            .actorId(actorId)
            .reason(reason)
            .build();
        historyRepository.save(history);

        log.info("Order {}: {} → {} [{}]", order.getOrderNumber(), fromStatus, target, triggerEvent);
    }
}
