package com.umutyenidil.atlas.controller;

import com.umutyenidil.atlas.dto.request.ProductCreateRequestDTO;
import com.umutyenidil.atlas.dto.response.ProductResponseDTO;
import com.umutyenidil.atlas.dto.response.SuccessResponseDTO;
import com.umutyenidil.atlas.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<ProductResponseDTO>> createProduct(@Valid @RequestBody ProductCreateRequestDTO request) {
        var result = productService.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponseDTO.of(result));
    }
}
