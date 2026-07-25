package com.urbanshopper.domain.rating;

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
 * Rating API — implements endpoints from 10-api-specification.md §9.
 *
 * POST   /api/v1/ratings                       — Submit a rating (H-001/H-002)
 * GET    /api/v1/shoppers/{id}/ratings          — List shopper ratings
 * GET    /api/v1/shoppers/{id}/ratings/summary  — Rating summary with averages
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;
    private final JwtService jwtService;

    @PostMapping("/ratings")
    public ResponseEntity<ApiResponse<RatingDTO>> submitRating(
            @Valid @RequestBody SubmitRatingRequest req,
            @RequestHeader("Authorization") String auth) {
        var userId = extractUserId(auth);
        var role = extractRole(auth);
        var raterType = "CUSTOMER".equals(role) ? "customer" : "shopper";
        var rating = ratingService.submitRating(req.orderId(), userId, raterType, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(rating));
    }

    @GetMapping("/shoppers/{id}/ratings")
    public ResponseEntity<ApiResponse<List<RatingDTO>>> getShopperRatings(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(ratingService.getShopperRatings(id)));
    }

    @GetMapping("/shoppers/{id}/ratings/summary")
    public ResponseEntity<ApiResponse<ShopperRatingSummary>> getShopperRatingSummary(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(ratingService.getShopperRatingSummary(id)));
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
