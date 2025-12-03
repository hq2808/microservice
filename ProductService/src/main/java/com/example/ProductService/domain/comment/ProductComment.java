package com.example.ProductService.domain.comment;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "product_comments")
public class ProductComment {

    @Id
    private String id;

    private String variantId;
    private String productSku;   // Liên kết Product
    private String userId;      // Nếu đăng nhập
    private String username;    // Hiển thị tên người comment
    private String content;

    private List<String> images;

    @CreatedDate
    private Instant createdAt;

    private int vote;
    private boolean approved = true; // Nếu muốn quản lý comment trước khi hiển thị
}
