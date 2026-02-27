package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.dto.response.ProductResponseDTO;
import com.umutyenidil.atlas.entity.Product;
import com.umutyenidil.atlas.service.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DefaultProductMapper implements ProductMapper {

    @Override
    public ProductResponseDTO toResponse(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId().toString())
                .name(product.getName())
                .description(Optional.ofNullable(product.getDescription()))
                .build();
    }
}
