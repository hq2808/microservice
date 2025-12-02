package com.example.ProductService.service;

import com.example.ProductService.dto.ProductCommentDto;

import java.util.List;

public interface ProductCommentService {
    ProductCommentDto addComment(String productSku, String userId, String comment);
    List<ProductCommentDto> getComments(String productSku);
    void voteComment(String commentId, int voteChange); // vote +1 / -1
}
