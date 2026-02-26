package com.umutyenidil.atlas.controller;

import com.umutyenidil.atlas.dto.request.VariantOptionValueCreateRequestDTO;
import com.umutyenidil.atlas.dto.response.SuccessResponseDTO;
import com.umutyenidil.atlas.dto.response.VariantOptionValueResponseDTO;
import com.umutyenidil.atlas.service.VariantOptionValueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/variant-option-values")
public class VariantOptionValueController {

    private final VariantOptionValueService variantOptionValueService;

    public VariantOptionValueController(VariantOptionValueService variantOptionValueService) {
        this.variantOptionValueService = variantOptionValueService;
    }

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<VariantOptionValueResponseDTO>> createVariantOptionValue(
            @Valid @RequestBody VariantOptionValueCreateRequestDTO request
    ) {
        var result = variantOptionValueService.createVariantOptionValue(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponseDTO.of(result));
    }
}
