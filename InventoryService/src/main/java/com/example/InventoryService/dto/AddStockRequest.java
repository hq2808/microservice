package com.example.InventoryService.dto;

import lombok.Data;

@Data
public class AddStockRequest {
    private String variantId;
    private int quantity;
}
