package com.example.ProductService.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProductVariantDto {
    private String id;
    private String productSku;
    private double price;
    private Double salePrice;
    private Map<String, String> attributes;
    private List<String> images;
}