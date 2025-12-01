package com.example.ProductService.service;

import com.example.ProductService.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product create(Product product);
    List<Product> findAll();
    Optional<Product> findById(String id);
    void deleteById(String id);
    Product update(String id, Product product);
}
