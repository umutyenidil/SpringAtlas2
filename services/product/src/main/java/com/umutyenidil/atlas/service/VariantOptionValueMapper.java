package com.umutyenidil.atlas.service;

import com.umutyenidil.atlas.dto.response.VariantOptionValueResponseDTO;
import com.umutyenidil.atlas.entity.VariantOptionValue;

public interface VariantOptionValueMapper {

    VariantOptionValueResponseDTO toResponse(VariantOptionValue entity);
}
