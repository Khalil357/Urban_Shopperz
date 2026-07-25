package com.urbanshopper.domain.dispute;

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
 * Dispute API — implements endpoints from 10-api-specification.md §8.
 *
 * POST   /api/v1/disputes                  — File a dispute (G-011)
 * GET    /api/v1/disputes/{id}              — Get dispute details
 * POST   /api/v1/disputes/{id}/evidence    — Add evidence
 * POST   /api/v1/disputes/{id}/escalate    — Escalate to ops admin
 * POST   /api/v1/disputes/{id}/resolve     — Resolve (admin)
 * POST   /api/v1/disputes/{id}/close       — Close resolved dispute
 */
@RestController
@RequestMapping("/api/v1/disputes")
@RequiredArgsConstructor
public class DisputeController {

    private final DisputeService disputeService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<ApiResponse<DisputeDTO>> createDispute(
            @Valid @RequestBody CreateDisputeRequest req,
            @RequestHeader("Authorization") String auth) {
        var userId = extractUserId(auth);
        var role = extractRole(auth);
        var filedByType = "CUSTOMER".equals(role) ? "customer" : "shopper";
        var dispute = disputeService.createDispute(req, userId, filedByType);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(dispute));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DisputeDTO>> getDispute(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(disputeService.getDispute(id)));
    }

    @PostMapping("/{id}/evidence")
    public ResponseEntity<ApiResponse<DisputeDTO>> addEvidence(
            @PathVariable UUID id,
            @Valid @RequestBody EvidenceUploadRequest req,
            @RequestHeader("Authorization") String auth) {
        var userId = extractUserId(auth);
        var role = extractRole(auth);
        var uploadedByType = "CUSTOMER".equals(role) ? "customer" : "shopper";
        // Ensure path id matches body disputeId
        var actualReq = new EvidenceUploadRequest(id, req.evidenceType(), req.content(), req.description());
        var dispute = disputeService.addEvidence(actualReq, userId, uploadedByType);
        return ResponseEntity.ok(ApiResponse.success(dispute));
    }

    @PostMapping("/{id}/escalate")
    public ResponseEntity<ApiResponse<DisputeDTO>> escalateDispute(@PathVariable UUID id) {
        var dispute = disputeService.escalateDispute(id);
        return ResponseEntity.ok(ApiResponse.success(dispute));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<DisputeDTO>> resolveDispute(
            @PathVariable UUID id,
            @Valid @RequestBody ResolveDisputeRequest req,
            @RequestHeader("Authorization") String auth) {
        var userId = extractUserId(auth);
        var dispute = disputeService.resolveDispute(id, req, userId);
        return ResponseEntity.ok(ApiResponse.success(dispute));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<ApiResponse<DisputeDTO>> closeDispute(@PathVariable UUID id) {
        var dispute = disputeService.closeDispute(id);
        return ResponseEntity.ok(ApiResponse.success(dispute));
    }

    private UUID extractUserId(String authHeader) {
        var token = authHeader.substring(7);
        return UUID.fromString(jwtService.validateToken(token).getSubject());
    }

    private String extractRole(String authHeader) {
        var token = authHeader.substring(7);
        return jwtService.validateToken(token).get("role", String.class);
    }
}
