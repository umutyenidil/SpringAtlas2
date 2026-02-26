package com.umutyenidil.atlas.service;

import com.umutyenidil.atlas.dto.response.PageResponseDTO;
import com.umutyenidil.atlas.dto.response.VariantOptionResponseDTO;
import com.umutyenidil.atlas.entity.VariantOption;
import org.springframework.data.domain.Page;

public interface VariantOptionMapper {

    VariantOptionResponseDTO toResponse(VariantOption entity);

    PageResponseDTO<VariantOptionResponseDTO> toPageResponse(Page<VariantOption> page);
}
