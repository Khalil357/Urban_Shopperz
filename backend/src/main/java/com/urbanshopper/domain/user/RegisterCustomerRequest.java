package com.urbanshopper.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterCustomerRequest(
    @NotBlank @Pattern(regexp = "^255[0-9]{9}$") String phone,
    @NotBlank @Size(min = 2, max = 100) String name,
    String language) {}
