package com.urbanshopper.domain.user;

import lombok.Builder;

@Builder
public record CustomerDTO(String id, String name, String phone, String language,
    String status, Integer trustScore, Integer totalOrders, String createdAt) {
    public static CustomerDTO fromEntity(Customer c) {
        return CustomerDTO.builder().id(c.getId().toString()).name(c.getName())
            .phone(c.getPhone()).language(c.getLanguage()).status(c.getStatus())
            .trustScore(c.getTrustScore()).totalOrders(c.getTotalOrders())
            .createdAt(c.getCreatedAt().toString()).build();
    }
}
