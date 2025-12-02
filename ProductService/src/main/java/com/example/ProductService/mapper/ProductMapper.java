package com.example.ProductService.mapper;

import com.example.ProductService.dto.ProductCommentDto;
import com.example.ProductService.dto.ProductDto;
import com.example.ProductService.model.Product;
import com.example.ProductService.model.ProductComment;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDto toDto(Product product) {
        if (product == null) return null;
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setSku(product.getSku());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setImages(product.getImages());
        dto.setVersion(product.getVersion());
        dto.setSoldCount(product.getSoldCount());
        dto.setShareCount(product.getShareCount());
        dto.setCreatedBy(product.getCreatedBy());
        dto.setUpdatedBy(product.getUpdatedBy());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }

    public Product toEntity(ProductDto dto) {
        if (dto == null) return null;
        Product product = new Product();
        product.setId(dto.getId());
        product.setSku(dto.getSku());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setImages(dto.getImages());
        product.setVersion(dto.getVersion());
        product.setSoldCount(dto.getSoldCount());
        product.setShareCount(dto.getShareCount());
        product.setCreatedBy(dto.getCreatedBy());
        product.setUpdatedBy(dto.getUpdatedBy());
        product.setCreatedAt(dto.getCreatedAt());
        product.setUpdatedAt(dto.getUpdatedAt());
        return product;
    }

    public ProductCommentDto toCommentDto(ProductComment comment) {
        ProductCommentDto dto = new ProductCommentDto();
        dto.setId(comment.getId());
        dto.setProductSku(comment.getProductSku());
        dto.setUserId(comment.getUserId());
        dto.setContent(comment.getContent());
        dto.setUsername(comment.getUsername());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setVote(comment.getVote());
        return dto;
    }
}
