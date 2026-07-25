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
 * POST   /api/v1/orders                           — Create order
 * GET    /api/v1/orders/{id}                       — Get order details
 * GET    /api/v1/orders/{id}/status                — Status summary + timeline
 * GET    /api/v1/orders/{id}/items                 — List all items
 * POST   /api/v1/orders/{id}/items/{itemId}/status — Update item status (D-006)
 * POST   /api/v1/orders/{id}/receipt               — Upload receipt (D-008)
 * POST   /api/v1/orders/{id}/arrive                — Shopper arrival at market
 * POST   /api/v1/orders/{id}/cancel                — Cancel order
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtService jwtService;

    // ── 5.1 Create Order ──

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(
            @Valid @RequestBody CreateOrderRequest req,
            @RequestHeader("Authorization") String auth) {
        var customerId = extractUserId(auth);
        var order = orderService.createOrder(req, customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(order));
    }

    // ── 5.2 Get Order ──

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrder(id)));
    }

    // ── 5.3 Get Order Status + Timeline ──

    @GetMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> getOrderStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderStatus(id)));
    }

    // ── 5.4 Get Order Items ──

    @GetMapping("/{id}/items")
    public ResponseEntity<ApiResponse<List<OrderItemDTO>>> getOrderItems(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderItems(id)));
    }

    // ── 5.5 Update Item Status (D-006, D-007) ──

    @PostMapping("/{orderId}/items/{itemId}/status")
    public ResponseEntity<ApiResponse<OrderItemDTO>> updateItemStatus(
            @PathVariable UUID orderId,
            @PathVariable UUID itemId,
            @Valid @RequestBody ItemStatusUpdateRequest req,
            @RequestHeader("Authorization") String auth) {
        var userId = extractUserId(auth);
        var item = orderService.updateItemStatus(orderId, itemId, req, userId);
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    // ── 5.6 Upload Receipt (D-008) ──

    @PostMapping("/{id}/receipt")
    public ResponseEntity<ApiResponse<ReceiptDTO>> uploadReceipt(
            @PathVariable UUID id,
            @Valid @RequestBody ReceiptUploadRequest req,
            @RequestHeader("Authorization") String auth) {
        var userId = extractUserId(auth);
        var receipt = orderService.uploadReceipt(id, req, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(receipt));
    }

    // ── 5.7 Shopper Arrival at Market ──

    @PostMapping("/{id}/arrive")
    public ResponseEntity<ApiResponse<OrderDTO>> arriveAtMarket(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String auth) {
        var shopperId = extractUserId(auth);
        var order = orderService.arriveAtMarket(id, shopperId);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    // ── 5.8 Cancel Order ──

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

    // ── Helpers ──

    private UUID extractUserId(String authHeader) {
        var token = authHeader.substring(7);
        return UUID.fromString(jwtService.validateToken(token).getSubject());
    }
}
