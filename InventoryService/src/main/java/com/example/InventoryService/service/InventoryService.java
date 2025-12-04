package com.example.InventoryService.service;

import com.example.InventoryService.dto.ReserveStockRequest;
import com.example.InventoryService.dto.UpdateStockRequest;
import com.example.InventoryService.entity.Inventory;

public interface InventoryService {
    Inventory addStock(String variantId, int quantity);
    boolean reserveStock(ReserveStockRequest req);
    void confirmStock(UpdateStockRequest req);
    void releaseStock(UpdateStockRequest req);
    Inventory getInv(String variantId);
}
