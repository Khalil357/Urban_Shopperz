package com.urbanshopper.domain.assignment;

import com.urbanshopper.shared.exception.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Internal Assignment API — endpoints for the assignment engine lifecycle.
 *
 * POST   /api/v1/assignments/trigger/{orderId}  — Manually trigger assignment
 * POST   /api/v1/assignments/cascade-timeout     — Force cascade timeout check
 */
@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentEngine assignmentEngine;

    /**
     * Manually trigger assignment for a specific order.
     * Called internally when order enters QUEUED_FOR_ASSIGNMENT.
     */
    @PostMapping("/trigger/{orderId}")
    public ResponseEntity<ApiResponse<String>> triggerAssignment(@PathVariable UUID orderId) {
        assignmentEngine.processOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success("Assignment processed for order " + orderId));
    }
}
