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

@Builder
public record CreateOrderRequest(
    @NotNull UUID customerId,
    @NotNull UUID zoneId,
    UUID marketId,
    @NotBlank String shoppingPreference,
    @NotBlank String deliveryPreference,
    Instant scheduledWindowStart,
    Instant scheduledWindowEnd,
    @NotBlank String paymentMethod,
    BigDecimal deliveryLat,
    BigDecimal deliveryLng,
    @NotBlank String deliveryAddressText,
    String deliveryLandmark,
    @Valid @Size(min = 1) List<ItemRequest> items
) {

    public record ItemRequest(
        @NotBlank String name,
        @NotNull Integer quantity,
        String unit,
        String preferredBrand,
        Integer maxPrice,
        String notes
    ) {}
}
