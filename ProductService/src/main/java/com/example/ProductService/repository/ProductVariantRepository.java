package com.example.ProductService.repository;

import com.example.ProductService.domain.product.ProductVariant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductVariantRepository extends MongoRepository<ProductVariant, String> {
    Optional<ProductVariant> findByProductSku(String productSku);
}
