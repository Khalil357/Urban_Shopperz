package com.urbanshopper.domain.delivery;

import com.urbanshopper.shared.exception.ApiResponse;
import com.urbanshopper.shared.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Delivery API — endpoints for the delivery flow (E-002 through E-009).
 *
 * POST   /api/v1/orders/{orderId}/delivery/start          — Start delivery (E-003)
 * POST   /api/v1/orders/{orderId}/delivery/confirm         — Confirm delivery (E-002)
 * POST   /api/v1/orders/{orderId}/delivery/report-delay    — Report delay (E-009)
 * POST   /api/v1/orders/{orderId}/delivery/customer-unavailable — Customer unavailable (E-004)
 * POST   /api/v1/orders/{orderId}/delivery/complete        — Complete delivery (E-007)
 * GET    /api/v1/orders/{orderId}/delivery/eta             — Get delivery ETA (E-003)
 */
@RestController
@RequestMapping("/api/v1/orders/{orderId}/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final JwtService jwtService;

    /**
     * Start delivery — shopper leaves market toward customer (E-003).
     * Transitions order: RECEIPT_VERIFIED → IN_DELIVERY.
     */
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<DeliveryDTO>> startDelivery(
            @PathVariable UUID orderId,
            @Valid @RequestBody DeliveryStartRequest req,
            @RequestHeader("Authorization") String auth) {
        var shopperId = extractUserId(auth);
        var delivery = deliveryService.startDelivery(orderId, req, shopperId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(delivery));
    }

    /**
     * Confirm delivery — shopper delivers items, collects evidence (E-002).
     * Transitions order: IN_DELIVERY → DELIVERED.
     * Sets inspection window deadline (E-007).
     */
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<DeliveryDTO>> confirmDelivery(
            @PathVariable UUID orderId,
            @Valid @RequestBody DeliveryConfirmRequest req,
            @RequestHeader("Authorization") String auth) {
        var shopperId = extractUserId(auth);
        var delivery = deliveryService.confirmDelivery(orderId, req, shopperId);
        return ResponseEntity.ok(ApiResponse.success(delivery));
    }

    /**
     * Report delay — shopper encounters traffic, road closure, etc. (E-009).
     * Recalculates ETA and records delay reason.
     */
    @PostMapping("/report-delay")
    public ResponseEntity<ApiResponse<DeliveryDTO>> reportDelay(
            @PathVariable UUID orderId,
            @Valid @RequestBody DeliveryDelayRequest req,
            @RequestHeader("Authorization") String auth) {
        var shopperId = extractUserId(auth);
        var delivery = deliveryService.reportDelay(orderId, req, shopperId);
        return ResponseEntity.ok(ApiResponse.success(delivery));
    }

    /**
     * Customer unavailable — shopper arrives but customer is not present (E-004).
     * After contact attempt, supports safe drop or items return.
     */
    @PostMapping("/customer-unavailable")
    public ResponseEntity<ApiResponse<DeliveryDTO>> reportCustomerUnavailable(
            @PathVariable UUID orderId,
            @RequestBody(required = false) CustomerUnavailableRequest req,
            @RequestHeader("Authorization") String auth) {
        var shopperId = extractUserId(auth);
        var safeDrop = req != null ? req.safeDropLocation() : null;
        var delivery = deliveryService.reportCustomerUnavailable(orderId, safeDrop, shopperId);
        return ResponseEntity.ok(ApiResponse.success(delivery));
    }

    /**
     * Complete delivery — after inspection window elapses (E-007).
     * Transitions order: DELIVERED → COMPLETED.
     */
    @PostMapping("/complete")
    public ResponseEntity<ApiResponse<DeliveryDTO>> completeDelivery(
            @PathVariable UUID orderId,
            @RequestHeader("Authorization") String auth) {
        var userId = extractUserId(auth);
        var delivery = deliveryService.completeDelivery(orderId, userId);
        return ResponseEntity.ok(ApiResponse.success(delivery));
    }

    /**
     * Get delivery ETA — current estimated arrival time (E-003).
     */
    @GetMapping("/eta")
    public ResponseEntity<ApiResponse<DeliveryEtaResponse>> getDeliveryEta(
            @PathVariable UUID orderId) {
        var eta = deliveryService.getDeliveryEta(orderId);
        return ResponseEntity.ok(ApiResponse.success(eta));
    }

    private UUID extractUserId(String authHeader) {
        var token = authHeader.substring(7);
        return UUID.fromString(jwtService.validateToken(token).getSubject());
    }

    private record CustomerUnavailableRequest(String safeDropLocation) {}
}
