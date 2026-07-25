package com.urbanshopper.domain.dispute;

import com.urbanshopper.domain.order.OrderRepository;
import com.urbanshopper.domain.order.OrderStatus;
import com.urbanshopper.domain.payment.PaymentService;
import com.urbanshopper.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Dispute Service — unified dispute resolution framework (G-011).
 *
 * Lifecycle: Reported → Review → Evidence → Decision → Resolved → Closed
 * Auto-triage (L-008): low-value+clear → automated, high-value → ops escalation
 * Integration: resolved disputes trigger refunds via PaymentService
 */
@Service
@RequiredArgsConstructor
public class DisputeService {

    private static final Logger log = LoggerFactory.getLogger(DisputeService.class);

    private static final int AUTO_RESOLVE_THRESHOLD = 10_000;    // TZS
    private static final int ESCALATION_THRESHOLD = 100_000;     // TZS

    private final DisputeRepository disputeRepository;
    private final DisputeEvidenceRepository evidenceRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    // ═══════════════════════════════════════════════
    //  Create Dispute (G-011 Step 1)
    // ═══════════════════════════════════════════════

    /**
     * File a dispute against an order.
     * Auto-routes based on type and value (L-008):
     * - item_discrepancy < 10k TZS + clear → automated validation
     * - shopper_behaviour → immediate ops escalation
     * - value > 100k TZS → ops escalation
     */
    @Transactional
    public DisputeDTO createDispute(CreateDisputeRequest req, UUID filedBy, String filedByType) {
        var order = orderRepository.findById(req.orderId())
            .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Order not found"));

        // Check for existing dispute
        if (disputeRepository.findByOrderId(req.orderId()).isPresent()) {
            throw new BusinessException("DUPLICATE_DISPUTE", "A dispute already exists for this order");
        }

        // Determine respondent
        UUID respondentId;
        String respondentType;
        if ("customer".equals(filedByType)) {
            respondentId = order.getShopperId();
            respondentType = "shopper";
        } else {
            respondentId = order.getCustomerId();
            respondentType = "customer";
        }

        var dispute = Dispute.builder()
            .orderId(req.orderId())
            .disputeType(req.disputeType())
            .filedBy(filedBy)
            .filedByType(filedByType)
            .respondentId(respondentId)
            .respondentType(respondentType)
            .reason(req.reason())
            .requestedRefund(req.requestedRefund())
            .build();

        // L-008: Auto-triage based on type and value
        routeDispute(dispute, req);

        var saved = disputeRepository.save(dispute);
        log.info("Dispute created: order={}, type={}, filed_by={}, status={}",
            req.orderId(), req.disputeType(), filedByType, saved.getStatus());

        return DisputeDTO.fromEntity(saved);
    }

    // ═══════════════════════════════════════════════
    //  Evidence (G-011 Step 2)
    // ═══════════════════════════════════════════════

    @Transactional
    public DisputeDTO addEvidence(EvidenceUploadRequest req, UUID uploadedBy, String uploadedByType) {
        var dispute = disputeRepository.findById(req.disputeId())
            .orElseThrow(() -> new BusinessException("DISPUTE_NOT_FOUND", "Dispute not found"));

        var evidence = DisputeEvidence.builder()
            .disputeId(dispute.getId())
            .evidenceType(req.evidenceType())
            .content(req.content())
            .description(req.description())
            .uploadedBy(uploadedBy)
            .uploadedByType(uploadedByType)
            .build();
        evidenceRepository.save(evidence);

        // Move to EVIDENCE_COLLECTION if currently in REPORTED or UNDER_REVIEW
        if (dispute.getStatus() == DisputeStatus.REPORTED
            || dispute.getStatus() == DisputeStatus.UNDER_REVIEW) {
            transition(dispute, DisputeStatus.EVIDENCE_COLLECTION,
                "Evidence submitted by " + uploadedByType);
        }

        var saved = disputeRepository.save(dispute);
        return DisputeDTO.fromEntity(saved);
    }

    // ═══════════════════════════════════════════════
    //  Resolve Dispute (G-011 Steps 4-6)
    // ═══════════════════════════════════════════════

    /**
     * Resolve a dispute — support agent or auto-resolution.
     * Triggers refund via PaymentService when applicable.
     */
    @Transactional
    public DisputeDTO resolveDispute(UUID disputeId, ResolveDisputeRequest req, UUID resolvedBy) {
        var dispute = disputeRepository.findById(disputeId)
            .orElseThrow(() -> new BusinessException("DISPUTE_NOT_FOUND", "Dispute not found"));

        if (dispute.getStatus() != DisputeStatus.DECISION
            && dispute.getStatus() != DisputeStatus.AUTOMATED_VALIDATION
            && dispute.getStatus() != DisputeStatus.UNDER_REVIEW) {
            throw new BusinessException("INVALID_STATE",
                "Dispute must be in DECISION, AUTOMATED_VALIDATION, or UNDER_REVIEW state");
        }

        dispute.setResolution(req.resolution());
        dispute.setResolutionNotes(req.resolutionNotes());
        dispute.setRefundAmount(req.refundAmount());
        dispute.setCompensationAmount(req.compensationAmount());
        dispute.setResolvedAt(Instant.now());

        transition(dispute, DisputeStatus.RESOLVED,
            "Resolved by " + resolvedBy + ": " + req.resolution());

        // Execute refund if applicable (G-006, G-007, G-008)
        if (req.refundAmount() != null && req.refundAmount() > 0) {
            try {
                paymentService.refund(dispute.getOrderId(),
                    req.refundAmount(),
                    "Dispute " + dispute.getId() + ": " + req.resolution());
                log.info("Refund of {} TZS executed for order {} (dispute {})",
                    req.refundAmount(), dispute.getOrderId(), dispute.getId());
            } catch (Exception e) {
                log.error("Refund failed for dispute {}: {}", dispute.getId(), e.getMessage());
                dispute.setResolutionNotes(
                    (req.resolutionNotes() != null ? req.resolutionNotes() + ". " : "")
                    + "WARNING: Refund execution failed: " + e.getMessage());
            }
        }

        var saved = disputeRepository.save(dispute);
        return DisputeDTO.fromEntity(saved);
    }

    /**
     * Close a resolved dispute (G-011 Step 7).
     */
    @Transactional
    public DisputeDTO closeDispute(UUID disputeId) {
        var dispute = disputeRepository.findById(disputeId)
            .orElseThrow(() -> new BusinessException("DISPUTE_NOT_FOUND", "Dispute not found"));

        if (dispute.getStatus() != DisputeStatus.RESOLVED) {
            throw new BusinessException("INVALID_STATE", "Dispute must be RESOLVED to close");
        }

        transition(dispute, DisputeStatus.CLOSED, "Closed");
        dispute.setClosedAt(Instant.now());
        var saved = disputeRepository.save(dispute);
        return DisputeDTO.fromEntity(saved);
    }

    /**
     * Escalate a dispute to operations admin.
     */
    @Transactional
    public DisputeDTO escalateDispute(UUID disputeId) {
        var dispute = disputeRepository.findById(disputeId)
            .orElseThrow(() -> new BusinessException("DISPUTE_NOT_FOUND", "Dispute not found"));

        transition(dispute, DisputeStatus.ESCALATED_TO_OPS,
            "Escalated to operations admin");
        dispute.setEscalatedAt(Instant.now());
        var saved = disputeRepository.save(dispute);
        return DisputeDTO.fromEntity(saved);
    }

    // ═══════════════════════════════════════════════
    //  Read Operations
    // ═══════════════════════════════════════════════

    @Transactional(readOnly = true)
    public DisputeDTO getDispute(UUID id) {
        return disputeRepository.findById(id)
            .map(DisputeDTO::fromEntity)
            .orElseThrow(() -> new BusinessException("DISPUTE_NOT_FOUND", "Dispute not found"));
    }

    @Transactional(readOnly = true)
    public List<DisputeDTO> getUserDisputes(UUID userId) {
        return disputeRepository.findByFiledByOrderByCreatedAtDesc(userId)
            .stream()
            .map(DisputeDTO::fromEntity)
            .toList();
    }

    // ═══════════════════════════════════════════════
    //  Private
    // ═══════════════════════════════════════════════

    /**
     * L-008: Auto-triage based on dispute type and value.
     */
    private void routeDispute(Dispute dispute, CreateDisputeRequest req) {
        // Shopper behaviour → always escalate (immediate suspension)
        if ("shopper_behaviour".equals(req.disputeType())) {
            dispute.setStatus(DisputeStatus.ESCALATED_TO_OPS);
            return;
        }

        // High value → escalate
        if (req.requestedRefund() != null && req.requestedRefund() > ESCALATION_THRESHOLD) {
            dispute.setStatus(DisputeStatus.ESCALATED_TO_OPS);
            return;
        }

        // Low value + clear type → automated validation
        if (req.requestedRefund() != null && req.requestedRefund() <= AUTO_RESOLVE_THRESHOLD
            && List.of("item_discrepancy", "payment_failure").contains(req.disputeType())) {
            dispute.setStatus(DisputeStatus.AUTOMATED_VALIDATION);
            return;
        }

        // Standard → manual review
        dispute.setStatus(DisputeStatus.UNDER_REVIEW);
    }

    private void transition(Dispute dispute, DisputeStatus target, String reason) {
        if (!dispute.getStatus().canTransitionTo(target)) {
            throw new BusinessException("INVALID_TRANSITION",
                "Cannot transition from " + dispute.getStatus() + " to " + target);
        }
        log.info("Dispute {}: {} → {} ({})", dispute.getId(), dispute.getStatus(), target, reason);
        dispute.setStatus(target);
    }
}
