package com.urbanshopper.domain.order;

import com.urbanshopper.shared.exception.ApiResponse;
import com.urbanshopper.shared.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Order API — implements endpoints from 10-api-specification.md §5.
 *
 * POST   /api/v1/orders              — Create order (D-001 → D-004 → F-003)
 * GET    /api/v1/orders/{id}          — Get order details
 * PATCH  /api/v1/orders/{id}/cancel   — Cancel order (G-001 through G-005)
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtService jwtService;

    /**
     * 5.1 Create Order
     *
     * Validates the request, calculates pricing, places payment pre-auth hold,
     * and returns the created order with estimated cost breakdown.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(
            @Valid @RequestBody CreateOrderRequest req,
            @RequestHeader("Authorization") String auth) {
        var customerId = extractUserId(auth);
        var order = orderService.createOrder(req, customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(order));
    }

    /**
     * 5.2 Get Order Status
     *
     * Returns full order details including status, pricing, items, and timeline.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrder(id)));
    }

    /**
     * Cancel an order.
     * Maps to PATCH for idempotency semantics (POST is also acceptable per spec).
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderDTO>> cancelOrder(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String auth,
            @RequestBody(required = false) CancelOrderRequest cancelReq) {
        var userId = extractUserId(auth);
        var reason = cancelReq != null ? cancelReq.reason() : "Customer requested";
        var order = orderService.cancelOrder(id, reason, "customer", userId);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    private UUID extractUserId(String authHeader) {
        var token = authHeader.substring(7); // Strip "Bearer "
        return UUID.fromString(jwtService.validateToken(token).getSubject());
    }
}
