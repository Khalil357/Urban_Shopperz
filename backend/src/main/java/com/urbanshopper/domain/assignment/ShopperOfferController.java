package com.urbanshopper.domain.assignment;

import com.urbanshopper.shared.exception.ApiResponse;
import com.urbanshopper.shared.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Shopper Offer API — endpoints for shoppers to view and respond to offers.
 *
 * GET    /api/v1/shoppers/{id}/offers                — List pending offers
 * POST   /api/v1/shoppers/{id}/offers/{offerId}/accept  — Accept offer
 * POST   /api/v1/shoppers/{id}/offers/{offerId}/decline — Decline offer
 * GET    /api/v1/shoppers/{id}/active-order            — Get current active order
 */
@RestController
@RequestMapping("/api/v1/shoppers/{shopperId}/offers")
@RequiredArgsConstructor
public class ShopperOfferController {

    private final AssignmentEngine assignmentEngine;
    private final OrderOfferRepository offerRepository;
    private final JwtService jwtService;

    /**
     * List pending offers for the shopper.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ShopperOfferDTO>>> getPendingOffers(
            @PathVariable UUID shopperId,
            @RequestHeader("Authorization") String auth) {
        verifyShopper(auth, shopperId);
        var offers = offerRepository.findByShopperIdAndStatus(shopperId, "pending");
        var dtos = offers.stream()
            .map(o -> ShopperOfferDTO.builder()
                .id(o.getId().toString())
                .orderId(o.getOrderId().toString())
                .status(o.getStatus())
                .score(o.getScore())
                .distanceKm(o.getDistanceKm())
                .offeredAt(o.getOfferedAt().toString())
                .expiresAt(o.getExpiresAt().toString())
                .cascadeRound(o.getCascadeRound())
                .build())
            .toList();
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    /**
     * Accept an offer — shopper commits to fulfilling the order.
     * Transitions order OFFERED → ACCEPTED.
     */
    @PostMapping("/{offerId}/accept")
    public ResponseEntity<ApiResponse<ShopperOfferDTO>> acceptOffer(
            @PathVariable UUID shopperId,
            @PathVariable UUID offerId,
            @RequestHeader("Authorization") String auth) {
        verifyShopper(auth, shopperId);
        var result = assignmentEngine.acceptOffer(offerId, shopperId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Decline an offer — passes to next eligible shopper via cascade.
     */
    @PostMapping("/{offerId}/decline")
    public ResponseEntity<ApiResponse<Void>> declineOffer(
            @PathVariable UUID shopperId,
            @PathVariable UUID offerId,
            @RequestHeader("Authorization") String auth) {
        verifyShopper(auth, shopperId);
        assignmentEngine.declineOffer(offerId, shopperId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    private void verifyShopper(String authHeader, UUID expectedId) {
        var token = authHeader.substring(7);
        var userId = UUID.fromString(jwtService.validateToken(token).getSubject());
        if (!userId.equals(expectedId)) {
            throw new SecurityException("Unauthorized access to shopper offers");
        }
    }
}
