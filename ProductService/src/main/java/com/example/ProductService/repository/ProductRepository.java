package com.example.ProductService.repository;

import com.example.ProductService.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product,String> {
    List<Product> findAllByActiveTrueAndDeletedFalse();
    Optional<Product> findByIdAndActiveTrueAndDeletedFalse(String id);
}
