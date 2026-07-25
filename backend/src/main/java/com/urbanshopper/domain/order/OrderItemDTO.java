package com.urbanshopper.domain.order;

import lombok.Builder;

/**
 * DTO for order item information exposed via API.
 */
@Builder
public record OrderItemDTO(
    String id,
    String name,
    Integer quantity,
    String unit,
    String preferredBrand,
    Integer maxPrice,
    String notes,
    String status,
    String substitutionNote,
    String substitutionApproval,
    Integer actualPrice,
    Boolean hasPhoto,
    Integer sortOrder
) {
    public static OrderItemDTO fromEntity(OrderItem item) {
        return OrderItemDTO.builder()
            .id(item.getId().toString())
            .name(item.getName())
            .quantity(item.getQuantity())
            .unit(item.getUnit())
            .preferredBrand(item.getPreferredBrand())
            .maxPrice(item.getMaxPrice())
            .notes(item.getNotes())
            .status(item.getStatus())
            .substitutionNote(item.getSubstitutionNote())
            .substitutionApproval(item.getSubstitutionApproval())
            .actualPrice(item.getActualPrice())
            .hasPhoto(item.getHasPhoto())
            .sortOrder(item.getSortOrder())
            .build();
    }
}
