package com.urbanshopper.domain.admin;

import lombok.Builder;

@Builder
public record AdminAuthDTO(String token, String username, String role, String name) {}
