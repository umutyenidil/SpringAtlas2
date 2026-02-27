package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.dto.request.VariantOptionCreateRequestDTO;
import com.umutyenidil.atlas.dto.response.PageResponseDTO;
import com.umutyenidil.atlas.dto.response.VariantOptionResponseDTO;
import com.umutyenidil.atlas.entity.VariantOption;
import com.umutyenidil.atlas.exception.NotFoundException;
import com.umutyenidil.atlas.exception.ValidationException;
import com.umutyenidil.atlas.repository.VariantOptionRepository;
import com.umutyenidil.atlas.service.VariantOptionMapper;
import com.umutyenidil.atlas.service.VariantOptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class DefaultVariantOptionService implements VariantOptionService {

    private final VariantOptionRepository variantOptionRepository;
    private final VariantOptionMapper variantOptionMapper;

    public DefaultVariantOptionService(VariantOptionRepository variantOptionRepository, VariantOptionMapper variantOptionMapper) {
        this.variantOptionRepository = variantOptionRepository;
        this.variantOptionMapper = variantOptionMapper;
    }

    @Override
    public VariantOptionResponseDTO createVariantOption(VariantOptionCreateRequestDTO request) {

        Optional<VariantOption> existingVariantOption = variantOptionRepository.findByName(request.name());

        if (existingVariantOption.isPresent()) {
            throw new ValidationException("name", "validation.variant-option.name.alreadyExists");
        }

        VariantOption savedVariantOption = variantOptionRepository.save(
                VariantOption.builder()
                        .name(request.name())
                        .build()
        );

        return variantOptionMapper.toResponse(savedVariantOption);
    }

    @Override
    public PageResponseDTO<VariantOptionResponseDTO> getVariantOptions(int page, int size) {

        Page<VariantOption> variantOptionPage = variantOptionRepository.findAll(Pageable.ofSize(size).withPage(page));

        return variantOptionMapper.toPageResponse(variantOptionPage);
    }

    @Override
    public VariantOptionResponseDTO getVariantOption(String id) {
        return variantOptionRepository.findById(UUID.fromString(id))
                .map(variantOptionMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("VARIANT_OPTION", "exception.variant-option.notFound"));
    }
}
