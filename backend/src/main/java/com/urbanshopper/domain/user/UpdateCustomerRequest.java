package com.urbanshopper.domain.user;

import lombok.Builder;

@Builder
public record UpdateCustomerRequest(
    String name,
    String language
) {}
