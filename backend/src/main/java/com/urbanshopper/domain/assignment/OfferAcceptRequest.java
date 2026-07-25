package com.urbanshopper.domain.assignment;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.util.UUID;

/**
 * Request body for POST /api/v1/shoppers/{shopperId}/offers/{offerId}/accept
 */
@Builder
public record OfferAcceptRequest(
    @NotNull UUID offerId
) {}
