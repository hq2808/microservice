package com.example.InventoryService.controller;

import com.example.InventoryService.dto.AddStockRequest;
import com.example.InventoryService.dto.InventoryResponse;
import com.example.InventoryService.dto.ReserveStockRequest;
import com.example.InventoryService.dto.UpdateStockRequest;
import com.example.InventoryService.entity.Inventory;
import com.example.InventoryService.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

    @PostMapping("/add")
    public ResponseEntity<InventoryResponse> addStock(@RequestBody AddStockRequest req) {
        Inventory inv = service.addStock(req.getVariantId(), req.getQuantity());
        InventoryResponse res = InventoryResponse.builder()
                .variantId(inv.getVariantId())
                .stock(inv.getStock())
                .reserved(inv.getReserved())
                .available(inv.getStock() - inv.getReserved())
                .build();
        return ResponseEntity.ok(res);
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserve(@RequestBody ReserveStockRequest req) {
        boolean ok = service.reserveStock(req);
        return ok ? ResponseEntity.ok().build()
                : ResponseEntity.status(409).body("Not enough stock");
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestBody UpdateStockRequest req) {
        service.confirmStock(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/release")
    public ResponseEntity<?> release(@RequestBody UpdateStockRequest req) {
        service.releaseStock(req);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{variantId}")
    public InventoryResponse get(@PathVariable String variantId) {
        Inventory inv = service.getInv(variantId);
        return InventoryResponse.builder()
                .variantId(inv.getVariantId())
                .stock(inv.getStock())
                .reserved(inv.getReserved())
                .available(inv.getStock() - inv.getReserved())
                .build();
    }
}
