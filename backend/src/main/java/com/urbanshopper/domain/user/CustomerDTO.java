package com.urbanshopper.domain.user;

import lombok.Builder;

@Builder
public record CustomerDTO(
    String id,
    String name,
    String phone,
    String language,
    String status,
    Integer trustScore,
    Integer totalOrders,
    String createdAt
) {
    public static CustomerDTO fromEntity(Customer customer) {
        return CustomerDTO.builder()
            .id(customer.getId().toString())
            .name(customer.getName())
            .phone(customer.getPhone())
            .language(customer.getLanguage())
            .status(customer.getStatus())
            .trustScore(customer.getTrustScore())
            .totalOrders(customer.getTotalOrders())
            .createdAt(customer.getCreatedAt().toString())
            .build();
    }
}
