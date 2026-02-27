package com.umutyenidil.atlas.service;

import com.umutyenidil.atlas.dto.request.VariantOptionValueCreateRequestDTO;
import com.umutyenidil.atlas.dto.response.VariantOptionValueResponseDTO;

public interface VariantOptionValueService {

    VariantOptionValueResponseDTO createVariantOptionValue(VariantOptionValueCreateRequestDTO request);
}
