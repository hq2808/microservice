package com.example.ProductService.service;

import com.example.ProductService.domain.product.ProductVariant;
import com.example.ProductService.dto.ProductVariantDto;

public interface ProductVariantService {
    ProductVariant create(ProductVariantDto dto);
    ProductVariant update(String id, ProductVariantDto dto);
}
