package com.example.InventoryService.dto;

import lombok.Data;

@Data
public class ReserveStockRequest {
    private String variantId;
    private int quantity;
}