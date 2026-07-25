package com.urbanshopper.domain.rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.util.UUID;

@Builder
public record SubmitRatingRequest(
    @NotNull UUID orderId,
    @NotNull @Min(1) @Max(5) Integer score,
    @Min(1) @Max(5) Integer itemAccuracy,
    @Min(1) @Max(5) Integer itemQuality,
    @Min(1) @Max(5) Integer timeliness,
    @Min(1) @Max(5) Integer communication,
    @Min(1) @Max(5) Integer professionalism,
    String feedback
) {}
