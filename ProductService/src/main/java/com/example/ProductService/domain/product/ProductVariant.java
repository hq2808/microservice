package com.example.ProductService.domain.product;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;

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
}
