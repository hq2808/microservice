package com.example.ProductService.service.impl;

import com.example.ProductService.dto.ProductDto;
import com.example.ProductService.mapper.ProductMapper;
import com.example.ProductService.model.Product;
import com.example.ProductService.repository.ProductRepository;
import com.example.ProductService.service.ProductService;
import com.example.common_security.security.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    @Override
    public Product create(Product product) {
        product.setCreatedBy(SecurityContextUtils.getUserId());
        product.setVersion(1);
        product.setDeleted(false);
        product.setSku(UUID.randomUUID().toString());
        return productRepository.save(product);
    }

    @Override
    public Product update(String id, Product updatedProduct) {
        Product oldProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // deactivate old version
        oldProduct.setActive(false);
        productRepository.save(oldProduct);

        // tạo version mới
        Product newVersion = new Product();
        newVersion.setSku(oldProduct.getSku());
        newVersion.setName(updatedProduct.getName());
        newVersion.setDescription(updatedProduct.getDescription());
        newVersion.setPrice(updatedProduct.getPrice());
        newVersion.setImages(updatedProduct.getImages());
        newVersion.setVersion(oldProduct.getVersion() + 1);
        newVersion.setCreatedBy(oldProduct.getCreatedBy());
        newVersion.setUpdatedBy(SecurityContextUtils.getUserId());
        newVersion.setDeleted(false);

        return productRepository.save(newVersion);
    }

    @Override
    public void softDelete(String id) {
        productRepository.findById(id).ifPresent(p -> {
            p.setDeleted(true);
            productRepository.save(p);
        });
    }

    @Override
    public ProductDto findById(String id) {
        Product product = productRepository.findByIdAndActiveTrueAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapper.toDto(product);
    }

    @Override
    public List<ProductDto> findAll() {
        return productRepository.findAllByActiveTrueAndDeletedFalse().stream()
                .map(mapper::toDto).toList();
    }

    @Override
    public void increaseSoldCount(String productId, long count) {
        productRepository.findById(productId).ifPresent(p -> {
            p.setSoldCount(p.getSoldCount() + count);
            productRepository.save(p);
        });
    }

    @Override
    public void increaseShareCount(String productId) {
        productRepository.findById(productId).ifPresent(p -> {
            p.setShareCount(p.getShareCount() + 1);
            productRepository.save(p);
        });
    }
}

