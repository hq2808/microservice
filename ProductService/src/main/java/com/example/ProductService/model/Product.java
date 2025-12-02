package com.example.ProductService.model;

import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "products")
public class Product {

    @Id
    private String id;
    private String sku;         // unique cho sản phẩm, không thay đổi khi update
    private String name;
    private String description;
    private double price;

    private List<String> images;       // Hình ảnh sản phẩm
    private int version;               // Phiên bản sản phẩm
    private boolean deleted = false;   // Xóa mềm
    private boolean active = true;        // chỉ 1 version active
    private long soldCount = 0;        // Lượt bán
    private int shareCount = 0;        // Lượt chia sẻ

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
