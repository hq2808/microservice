package com.example.InventoryService.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {
    private String variantId;
    private int stock;
    private int reserved;
    private int available;
}
