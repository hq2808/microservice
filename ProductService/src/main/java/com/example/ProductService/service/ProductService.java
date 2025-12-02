package com.example.ProductService.service;

import com.example.ProductService.dto.ProductDto;
import com.example.ProductService.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product create(Product product);
    Product update(String id, Product product);
    void softDelete(String id);
    ProductDto findById(String id);
    List<ProductDto> findAll();
    void increaseSoldCount(String productId, long count);
    void increaseShareCount(String productId);
}
