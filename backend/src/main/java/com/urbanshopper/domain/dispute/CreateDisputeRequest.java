package com.urbanshopper.domain.dispute;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.util.UUID;

@Builder
public record CreateDisputeRequest(
    @NotNull UUID orderId,
    @NotBlank String disputeType,
    @NotBlank String reason,
    Integer requestedRefund
) {}
