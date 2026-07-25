package com.urbanshopper.domain.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Request body for POST /api/v1/orders.
 * Matches the API specification in 10-api-specification.md §5.1.
 *
 * Business rules: D-001 (order creation requirements), D-002 (item entry), D-003 (pricing visibility).
 */
@Builder
public record CreateOrderRequest(

    @NotNull UUID zoneId,

    UUID marketId,

    @Valid @NotNull DeliveryLocation deliveryLocation,

    @NotBlank String shoppingPreference,

    @NotBlank String deliveryTime,

    Instant scheduledWindow,

    @NotBlank String paymentMethod,

    @NotBlank String substitutionDefault,

    @Valid @Size(min = 1) List<ItemRequest> items
) {

    public record DeliveryLocation(
        @NotNull BigDecimal latitude,
        @NotNull BigDecimal longitude,
        @NotBlank String addressText,
        String landmark
    ) {}

    public record ItemRequest(
        @NotBlank String name,
        @NotNull Integer quantity,
        String unit,
        String preferredBrand,
        Integer maxPrice,
        String notes
    ) {}
}
