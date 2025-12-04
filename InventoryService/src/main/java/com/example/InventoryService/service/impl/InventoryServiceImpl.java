package com.example.InventoryService.service.impl;

import com.example.InventoryService.dto.ReserveStockRequest;
import com.example.InventoryService.dto.UpdateStockRequest;
import com.example.InventoryService.entity.Inventory;
import com.example.InventoryService.repositories.InventoryRepository;
import com.example.InventoryService.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository repo;
    private final RedissonClient redisson;

    // Try to hold the lock for 200ms, wait max 150ms
    private static final long WAIT_TIME = 150;
    private static final long LEASE_TIME = 200;

    @Override
    public Inventory addStock(String variantId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");

        RLock lock = redisson.getLock("inventory:lock:" + variantId);

        try {
            if (!lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Cannot acquire lock, try again");
            }

            Inventory inv = repo.findByVariantId(variantId)
                    .orElseGet(() -> Inventory.builder()
                            .variantId(variantId)
                            .stock(0)
                            .reserved(0)
                            .warehouseId("default")
                            .build());

            inv.setStock(inv.getStock() + quantity);
            inv.setUpdatedAt(Instant.now());
            return repo.save(inv);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

    // -----------------------------
    // 1. Reserve Stock
    // -----------------------------
    public boolean reserveStock(ReserveStockRequest req) {
        RLock lock = redisson.getLock("inventory:lock:" + req.getVariantId());

        try {
            if (!lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.MILLISECONDS)) {
                return false;   // OrderService sẽ retry
            }

            Inventory inv = repo.findByVariantId(req.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found"));

            int available = inv.getStock() - inv.getReserved();
            if (available < req.getQuantity()) {
                return false;
            }

            inv.setReserved(inv.getReserved() + req.getQuantity());
            inv.setUpdatedAt(Instant.now());
            repo.save(inv);

            return true;

        } catch (InterruptedException e) {
            return false;

        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

    // -----------------------------
    // 2. Confirm Order (reduce stock)
    // -----------------------------
    public void confirmStock(UpdateStockRequest req) {
        Inventory inv = repo.findByVariantId(req.getVariantId())
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        inv.setStock(inv.getStock() - req.getQuantity());
        inv.setReserved(inv.getReserved() - req.getQuantity());
        inv.setUpdatedAt(Instant.now());

        repo.save(inv);
    }

    // -----------------------------
    // 3. Release stock (cancel order)
    // -----------------------------
    public void releaseStock(UpdateStockRequest req) {
        Inventory inv = repo.findByVariantId(req.getVariantId())
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        inv.setReserved(inv.getReserved() - req.getQuantity());
        inv.setUpdatedAt(Instant.now());

        repo.save(inv);
    }

    // -----------------------------
    // 4. Get inventory info
    // -----------------------------
    public Inventory getInv(String variantId) {
        return repo.findByVariantId(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
    }
}
