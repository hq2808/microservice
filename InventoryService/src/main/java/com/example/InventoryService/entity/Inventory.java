package com.example.InventoryService.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    private String id;

    private String variantId;      // ProductVariant (color, size...)

    private int stock;             // total stock
    private int reserved;          // stock reserved by pending orders

    private String warehouseId;

    private Instant updatedAt;
}
