package com.urbanshopper.domain.dispute;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.util.UUID;

@Builder
public record EvidenceUploadRequest(
    @NotNull UUID disputeId,
    @NotBlank String evidenceType,
    @NotBlank String content,
    String description
) {}
