package com.example.ProductService.domain.product;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Builder
@Data
public class ProductVariant {

    @Id
    private String id;
    private String productSku;               // unique per variant

    private double price;
    private Double salePrice;

    private Map<String, String> attributes;   // { "color": "red", "size": "M" }

    private List<String> images;

    private boolean active = true;
    private int version = 1;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
