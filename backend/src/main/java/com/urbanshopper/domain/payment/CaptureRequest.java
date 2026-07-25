package com.urbanshopper.domain.payment;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.util.UUID;

@Builder
public record CaptureRequest(
    @NotNull UUID orderId
) {}
