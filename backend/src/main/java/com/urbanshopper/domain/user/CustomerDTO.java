package com.urbanshopper.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

@Builder
record RegisterCustomerRequest(
    @NotBlank @Pattern(regexp = "^255[0-9]{9}$") String phone,
    @NotBlank @Size(min = 2, max = 100) String name,
    String language
) {}

@Builder
record UpdateCustomerRequest(
    String name,
    String language
) {}
