package com.urbanshopper.domain.payment;

import com.urbanshopper.shared.exception.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Payment API — implements endpoints from 10-api-specification.md §6.
 *
 * POST   /api/v1/payments/pre-auth       — Place pre-authorisation hold (F-003)
 * POST   /api/v1/payments/capture        — Capture payment after delivery (F-004)
 * POST   /api/v1/payments/refund         — Process refund (G-006, G-007)
 * GET    /api/v1/payments/{id}/status    — Payment status by order
 * GET    /api/v1/payments/{id}/transactions — Payment transaction history
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pre-auth")
    public ResponseEntity<ApiResponse<PaymentDTO>> preAuth(
            @Valid @RequestBody PreAuthRequest req) {
        var payment = paymentService.preAuth(req.orderId(), req.paymentMethod());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(payment));
    }

    @PostMapping("/capture")
    public ResponseEntity<ApiResponse<PaymentDTO>> capture(
            @Valid @RequestBody CaptureRequest req) {
        var payment = paymentService.capture(req.orderId());
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    @PostMapping("/refund")
    public ResponseEntity<ApiResponse<PaymentDTO>> refund(
            @Valid @RequestBody RefundRequest req) {
        var payment = paymentService.refund(req.orderId(), req.amount(), req.reason());
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<PaymentDTO>> getStatus(@PathVariable UUID orderId) {
        var payment = paymentService.getPaymentByOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    @GetMapping("/{paymentId}/transactions")
    public ResponseEntity<ApiResponse<List<PaymentTransactionDTO>>> getTransactions(
            @PathVariable UUID paymentId) {
        var transactions = paymentService.getPaymentTransactions(paymentId);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }
}
