package com.umutyenidil.atlas.service;

import com.umutyenidil.atlas.dto.request.VariantOptionCreateRequestDTO;
import com.umutyenidil.atlas.dto.response.PageResponseDTO;
import com.umutyenidil.atlas.dto.response.VariantOptionResponseDTO;

public interface VariantOptionService {

    VariantOptionResponseDTO createVariantOption(VariantOptionCreateRequestDTO request);

    PageResponseDTO<VariantOptionResponseDTO> getVariantOptions(int page, int size);

    VariantOptionResponseDTO getVariantOption(String id);
}
