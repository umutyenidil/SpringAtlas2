package com.umutyenidil.atlas.controller;

import com.umutyenidil.atlas.dto.response.ProductImageUploadResponseDTO;
import com.umutyenidil.atlas.dto.response.SuccessResponseDTO;
import com.umutyenidil.atlas.service.ProductImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/product-images")
public class ProductImageController {

    private final ProductImageService productImageService;

    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<SuccessResponseDTO<ProductImageUploadResponseDTO>> uploadImage(@RequestParam("file") MultipartFile file) {
        ProductImageUploadResponseDTO result = productImageService.upload(file);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponseDTO.of(result));
    }
}
