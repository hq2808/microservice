package com.example.ProductService.controller;

import com.example.ProductService.domain.product.ProductVariant;
import com.example.ProductService.dto.ProductVariantDto;
import com.example.ProductService.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product-variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantService service;

    @PostMapping
    public ResponseEntity<ProductVariant> create(@RequestBody ProductVariantDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductVariant> update(@PathVariable String id,
                                                 @RequestBody ProductVariantDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
}
