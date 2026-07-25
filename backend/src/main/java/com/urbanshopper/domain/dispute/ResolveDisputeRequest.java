package com.urbanshopper.domain.dispute;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ResolveDisputeRequest(
    @NotBlank String resolution,
    String resolutionNotes,
    Integer refundAmount,
    Integer compensationAmount
) {}
