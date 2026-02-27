package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.dto.response.PageResponseDTO;
import com.umutyenidil.atlas.dto.response.VariantOptionResponseDTO;
import com.umutyenidil.atlas.entity.VariantOption;
import com.umutyenidil.atlas.service.VariantOptionMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultVariantOptionMapper implements VariantOptionMapper {

    @Override
    public VariantOptionResponseDTO toResponse(VariantOption entity) {
        return VariantOptionResponseDTO.builder()
                .id(entity.getId().toString())
                .name(entity.getName())
                .build();
    }

    @Override
    public PageResponseDTO<VariantOptionResponseDTO> toPageResponse(Page<VariantOption> page) {
        List<VariantOptionResponseDTO> items = page.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return PageResponseDTO.<VariantOptionResponseDTO>builder()
                .items(items)
                .pageNumber(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .isLast(page.isLast())
                .build();
    }
}
