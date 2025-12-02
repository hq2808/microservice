package com.example.ProductService.repository;

import com.example.ProductService.model.ProductComment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCommentRepository extends MongoRepository<ProductComment, String> {
    List<ProductComment> findAllByProductSku(String productId);

}
