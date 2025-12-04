package com.example.InventoryService.repositories;

import com.example.InventoryService.entity.Inventory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface InventoryRepository extends MongoRepository<Inventory, String> {

    Optional<Inventory> findByVariantId(String variantId);

}
