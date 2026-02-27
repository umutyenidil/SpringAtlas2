package com.umutyenidil.atlas.controller;

import com.umutyenidil.atlas.dto.request.VariantOptionCreateRequestDTO;
import com.umutyenidil.atlas.dto.response.PageResponseDTO;
import com.umutyenidil.atlas.dto.response.SuccessResponseDTO;
import com.umutyenidil.atlas.dto.response.VariantOptionResponseDTO;
import com.umutyenidil.atlas.service.VariantOptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/variant-options")
@RestController
public class VariantOptionController {

    private final VariantOptionService variantOptionService;

    public VariantOptionController(VariantOptionService variantOptionService) {
        this.variantOptionService = variantOptionService;
    }

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<VariantOptionResponseDTO>> createVariantOption(
            @Valid @RequestBody VariantOptionCreateRequestDTO request
    ) {
        var result = variantOptionService.createVariantOption(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponseDTO.of(result));
    }

    @GetMapping
    public ResponseEntity<SuccessResponseDTO<PageResponseDTO<VariantOptionResponseDTO>>> getVariantOptions(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        var result = variantOptionService.getVariantOptions(page - 1, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponseDTO.of(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<VariantOptionResponseDTO>> getVariantOption(
            @PathVariable String id
    ) {
        var result = variantOptionService.getVariantOption(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponseDTO.of(result));
    }
}
