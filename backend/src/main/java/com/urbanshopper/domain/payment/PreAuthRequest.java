package com.urbanshopper.domain.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.util.UUID;

@Builder
public record PreAuthRequest(
    @NotNull UUID orderId,
    @NotBlank String paymentMethod
) {}
