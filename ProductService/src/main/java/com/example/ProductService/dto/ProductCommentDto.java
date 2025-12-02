package com.example.ProductService.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ProductCommentDto {
    private String id;
    private String productSku;   // Liên kết Product
    private String userId;      // Nếu đăng nhập
    private String username;    // Hiển thị tên người comment
    private String content;
    private Instant createdAt;
    private int vote;
    private boolean approved = true;
}
