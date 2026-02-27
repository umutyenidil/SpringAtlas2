package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.dto.response.VariantOptionValueResponseDTO;
import com.umutyenidil.atlas.entity.VariantOptionValue;
import com.umutyenidil.atlas.service.VariantOptionValueMapper;
import org.springframework.stereotype.Service;

@Service
public class DefaultVariantOptionValueMapper implements VariantOptionValueMapper {

    @Override
    public VariantOptionValueResponseDTO toResponse(VariantOptionValue entity) {
        return VariantOptionValueResponseDTO.builder()
                .id(entity.getId().toString())
                .variantOptionId(entity.getVariantOption().getId().toString())
                .name(entity.getName())
                .build();
    }
}
