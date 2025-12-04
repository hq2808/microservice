package com.example.InventoryService.dto;

import lombok.Data;

@Data
public class UpdateStockRequest {
    private String variantId;
    private int quantity;
}
