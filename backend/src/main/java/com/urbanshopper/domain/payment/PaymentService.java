package com.urbanshopper.domain.payment;

import com.urbanshopper.domain.order.*;
import com.urbanshopper.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Payment Service — orchestrates the financial lifecycle of orders.
 *
 * Business rules:
 * - F-003: Pre-authorisation hold on order creation
 * - F-004: Final amount calculation with variance approval
 * - F-005: Tiered shopper payout calculation
 * - F-006: 48-hour settlement period
 * - F-010: Shopper wallet management
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private static final int SETTLEMENT_DELAY_HOURS = 48;

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final ShopperWalletRepository walletRepository;
    private final OrderRepository orderRepository;
    private final OrderPricingService pricingService;
    private final List<PaymentProvider> providers;

    // ═══════════════════════════════════════════════
    //  Pre-Authorisation (F-003)
    // ═══════════════════════════════════════════════

    /**
     * F-003: Place a pre-authorisation hold on the customer's payment method.
     * Called automatically when an order enters QUEUED_FOR_ASSIGNMENT.
     *
     * @param orderId       the order to authorise payment for
     * @param paymentMethod mpesa | mixx | cod
     * @return payment DTO
     */
    @Transactional
    public PaymentDTO preAuth(UUID orderId, String paymentMethod) {
        var order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found: " + orderId));

        // Check if payment already exists
        var existing = paymentRepository.findByOrderId(orderId);
        if (existing.isPresent()) {
            return PaymentDTO.fromEntity(existing.get());
        }

        var provider = resolveProvider(paymentMethod);
        var reference = "URB-" + order.getOrderNumber() + "-PA";

        // Calculate fee breakdown using existing pricing
        var estimatedItemCost = order.getEstimatedItemCost();
        var serviceFee = pricingService.calculateServiceFee(estimatedItemCost);
        var deliveryFee = order.getEstimatedDeliveryFee();
        var estimatedTotal = estimatedItemCost + serviceFee + deliveryFee;

        // Call payment provider
        var customerRef = order.getCustomerId().toString(); // Will be customer phone in real impl
        var result = provider.preAuth(customerRef, estimatedTotal, reference);

        if (!result.success()) {
            throw new BusinessException("PRE_AUTH_FAILED",
                "Pre-authorisation failed: " + result.message());
        }

        // Create payment record
        var payment = Payment.builder()
            .orderId(orderId)
            .customerId(order.getCustomerId())
            .status(PaymentStatus.AUTHORIZED)
            .estimatedAmount(estimatedTotal)
            .serviceFee(serviceFee)
            .deliveryFee(deliveryFee)
            .itemCost(estimatedItemCost)
            .paymentMethod(paymentMethod)
            .provider(provider.getProviderName())
            .providerReference(result.providerReference())
            .providerStatus(result.message())
            .authorizedAt(Instant.now())
            .build();

        var saved = paymentRepository.save(payment);

        // Record transaction
        recordTransaction(saved.getId(), "pre_auth", estimatedTotal,
            provider.getProviderName(), result);

        log.info("Payment pre-auth: order={}, amount={} TZS, provider={}, ref={}",
            order.getOrderNumber(), estimatedTotal, provider.getProviderName(), result.providerReference());

        return PaymentDTO.fromEntity(saved);
    }

    // ═══════════════════════════════════════════════
    //  Capture (F-004)
    // ═══════════════════════════════════════════════

    /**
     * F-004: Capture payment after delivery confirmation.
     * Calculates final amounts, checks variance threshold, captures via provider.
     *
     * @param orderId the delivered order
     * @return payment DTO
     */
    @Transactional
    public PaymentDTO capture(UUID orderId) {
        var payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new BusinessException("PAYMENT_NOT_FOUND",
                "No payment record for order: " + orderId));

        if (payment.getStatus() != PaymentStatus.AUTHORIZED) {
            throw new BusinessException("INVALID_STATE",
                "Payment must be AUTHORIZED to capture. Current: " + payment.getStatus());
        }

        var order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found"));

        // Calculate final amounts
        var actualItemCost = order.getActualItemCost() != null ? order.getActualItemCost() : payment.getItemCost();
        var serviceFee = pricingService.calculateServiceFee(actualItemCost);
        var deliveryFee = payment.getDeliveryFee();
        var totalAmount = actualItemCost + serviceFee + deliveryFee;

        // D-003: Check variance threshold
        if (pricingService.requiresVarianceApproval(payment.getEstimatedAmount(), totalAmount)) {
            log.warn("Variance exceeds threshold for order {}: estimated={}, actual={}",
                order.getOrderNumber(), payment.getEstimatedAmount(), totalAmount);
            // In production, this would require customer approval before capture
        }

        var provider = resolveProvider(payment.getPaymentMethod());
        var reference = "URB-" + order.getOrderNumber() + "-CAP";

        var result = provider.capture(payment.getProviderReference(), totalAmount, reference);

        if (!result.success()) {
            throw new BusinessException("CAPTURE_FAILED",
                "Payment capture failed: " + result.message());
        }

        payment.setStatus(PaymentStatus.CAPTURED);
        payment.setCapturedAmount(totalAmount);
        payment.setServiceFee(serviceFee);
        payment.setItemCost(actualItemCost);
        payment.setShopperId(order.getShopperId());
        payment.setCapturedAt(Instant.now());
        payment.setProviderReference(result.providerReference());
        payment.setProviderStatus(result.message());

        // F-005: Calculate shopper payout
        var shopperPayout = calculateShopperPayout(order, deliveryFee);
        payment.setShopperPayout(shopperPayout);

        var saved = paymentRepository.save(payment);

        recordTransaction(saved.getId(), "capture", totalAmount,
            provider.getProviderName(), result);

        log.info("Payment captured: order={}, amount={} TZS, shopper_payout={} TZS",
            order.getOrderNumber(), totalAmount, shopperPayout);

        return PaymentDTO.fromEntity(saved);
    }

    // ═══════════════════════════════════════════════
    //  Refund (G-006, G-007)
    // ═══════════════════════════════════════════════

    /**
     * Process a full or partial refund.
     *
     * @param orderId    the order to refund
     * @param amount     amount to refund (null for full refund)
     * @param reason     reason for the refund
     * @return payment DTO
     */
    @Transactional
    public PaymentDTO refund(UUID orderId, Integer amount, String reason) {
        var payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new BusinessException("PAYMENT_NOT_FOUND",
                "No payment record for order: " + orderId));

        if (payment.getStatus() != PaymentStatus.CAPTURED && payment.getStatus() != PaymentStatus.SETTLED) {
            throw new BusinessException("INVALID_STATE",
                "Payment must be CAPTURED or SETTLED to refund");
        }

        var refundAmount = amount != null ? amount : payment.getCapturedAmount();
        var provider = resolveProvider(payment.getPaymentMethod());
        var reference = "URB-" + orderId.toString().substring(0, 8) + "-REF";

        var result = provider.refund(payment.getProviderReference(), refundAmount, reference);

        if (!result.success()) {
            throw new BusinessException("REFUND_FAILED",
                "Refund failed: " + result.message());
        }

        boolean isFullRefund = refundAmount >= (payment.getCapturedAmount() != null ? payment.getCapturedAmount() : 0);
        payment.setStatus(isFullRefund ? PaymentStatus.REFUNDED : PaymentStatus.PARTIAL_REFUNDED);
        if (isFullRefund) {
            payment.setCapturedAmount(payment.getCapturedAmount() - refundAmount);
        }

        var saved = paymentRepository.save(payment);

        recordTransaction(saved.getId(), "refund", refundAmount,
            provider.getProviderName(), result);

        log.info("Payment refunded: order={}, amount={} TZS, reason={}",
            orderId, refundAmount, reason);

        return PaymentDTO.fromEntity(saved);
    }

    // ═══════════════════════════════════════════════
    //  Settlement (F-006)
    // ═══════════════════════════════════════════════

    /**
     * Settle payment — transfers shopper payout to wallet.
     * Called by scheduled task 48 hours after capture.
     */
    @Transactional
    public PaymentDTO settle(UUID orderId) {
        var payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new BusinessException("PAYMENT_NOT_FOUND",
                "No payment record for order: " + orderId));

        if (payment.getStatus() != PaymentStatus.CAPTURED) {
            throw new BusinessException("INVALID_STATE",
                "Payment must be CAPTURED to settle. Current: " + payment.getStatus());
        }

        if (payment.getShopperId() == null || payment.getShopperPayout() == null) {
            log.warn("Cannot settle order {}: no shopper or payout amount", orderId);
            return PaymentDTO.fromEntity(payment);
        }

        // Create or update shopper wallet (F-010)
        var wallet = walletRepository.findByShopperId(payment.getShopperId())
            .orElseGet(() -> walletRepository.save(
                ShopperWallet.builder().shopperId(payment.getShopperId()).build()));

        wallet.setPendingBalance(wallet.getPendingBalance() - payment.getShopperPayout());
        wallet.setAvailableBalance(wallet.getAvailableBalance() + payment.getShopperPayout());
        wallet.setLifetimeEarnings(wallet.getLifetimeEarnings() + payment.getShopperPayout());
        walletRepository.save(wallet);

        payment.setStatus(PaymentStatus.SETTLED);
        payment.setSettledAt(Instant.now());
        var saved = paymentRepository.save(payment);

        log.info("Payment settled: order={}, shopper={}, payout={} TZS",
            orderId, payment.getShopperId(), payment.getShopperPayout());

        return PaymentDTO.fromEntity(saved);
    }

    /**
     * Scheduled task: settle payments 48 hours after capture.
     */
    @Scheduled(fixedDelay = 300_000) // every 5 minutes
    public void processSettlements() {
        var cutoff = Instant.now().minus(SETTLEMENT_DELAY_HOURS, ChronoUnit.HOURS);
        var captured = paymentRepository.findByStatus(PaymentStatus.CAPTURED);

        for (var payment : captured) {
            if (payment.getCapturedAt() != null && payment.getCapturedAt().isBefore(cutoff)) {
                try {
                    settle(payment.getOrderId());
                } catch (Exception e) {
                    log.error("Settlement failed for order {}: {}",
                        payment.getOrderId(), e.getMessage());
                }
            }
        }
    }

    // ═══════════════════════════════════════════════
    //  Read Operations
    // ═══════════════════════════════════════════════

    @Transactional(readOnly = true)
    public PaymentDTO getPaymentByOrder(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
            .map(PaymentDTO::fromEntity)
            .orElseThrow(() -> new BusinessException("PAYMENT_NOT_FOUND",
                "No payment for order: " + orderId));
    }

    @Transactional(readOnly = true)
    public List<PaymentTransactionDTO> getPaymentTransactions(UUID paymentId) {
        return transactionRepository.findByPaymentIdOrderByCreatedAtAsc(paymentId)
            .stream()
            .map(PaymentTransactionDTO::fromEntity)
            .toList();
    }

    // ═══════════════════════════════════════════════
    //  Private Helpers
    // ═══════════════════════════════════════════════

    private PaymentProvider resolveProvider(String paymentMethod) {
        return providers.stream()
            .filter(p -> p.getProviderName().equals(paymentMethod))
            .findFirst()
            .orElseThrow(() -> new BusinessException("PROVIDER_NOT_FOUND",
                "No payment provider for: " + paymentMethod));
    }

    private void recordTransaction(UUID paymentId, String type, int amount,
                                    String provider, PaymentProvider.ProviderResult result) {
        var tx = PaymentTransaction.builder()
            .paymentId(paymentId)
            .transactionType(type)
            .amount(amount)
            .provider(provider)
            .providerReference(result.providerReference())
            .providerResponse(result.rawResponse())
            .status(result.success() ? "completed" : "failed")
            .build();
        transactionRepository.save(tx);
    }

    /**
     * F-005: Calculate shopper payout.
     * Shopper receives: 100% delivery fee + tiered shopping fee.
     *
     * Tiers:
     * 0 — 50,000 TZS:      2,500 TZS
     * 50,001 — 150,000:    4,000 TZS
     * 150,001 — 300,000:   6,000 TZS
     * Above 300,000:       2% (max 15,000 TZS)
     */
    private int calculateShopperPayout(Order order, int deliveryFee) {
        var itemCost = order.getActualItemCost() != null ? order.getActualItemCost()
                     : order.getEstimatedItemCost();

        int shoppingFee;
        if (itemCost <= 50_000) {
            shoppingFee = 2_500;
        } else if (itemCost <= 150_000) {
            shoppingFee = 4_000;
        } else if (itemCost <= 300_000) {
            shoppingFee = 6_000;
        } else {
            shoppingFee = Math.min((int) Math.round(itemCost * 0.02), 15_000);
        }

        return deliveryFee + shoppingFee;
    }
}
