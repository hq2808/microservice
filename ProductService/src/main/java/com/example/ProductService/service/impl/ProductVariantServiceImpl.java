package com.example.ProductService.service.impl;

import com.example.ProductService.domain.product.ProductVariant;
import com.example.ProductService.dto.ProductVariantDto;
import com.example.ProductService.repository.ProductVariantRepository;
import com.example.ProductService.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository variantRepository;

    @Override
    public ProductVariant create(ProductVariantDto dto) {
        if (variantRepository.findByProductSku(dto.getProductSku()).isPresent()) {
            throw new RuntimeException("Product SKU already exists");
        }

        ProductVariant variant = ProductVariant.builder()
                .productSku(dto.getProductSku())
                .price(dto.getPrice())
                .salePrice(dto.getSalePrice())
                .attributes(dto.getAttributes())
                .images(dto.getImages())
                .active(true)
                .version(0)
                .build();

        return variantRepository.save(variant);
    }

    @Override
    public ProductVariant update(String id, ProductVariantDto dto) {
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        variant.setPrice(dto.getPrice());
        variant.setSalePrice(dto.getSalePrice());
        if (dto.getAttributes() != null) variant.setAttributes(dto.getAttributes());
        if (dto.getImages() != null) variant.setImages(dto.getImages());

        return variantRepository.save(variant);
    }
}
