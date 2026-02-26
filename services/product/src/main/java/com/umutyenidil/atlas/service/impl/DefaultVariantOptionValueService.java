package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.dto.request.VariantOptionValueCreateRequestDTO;
import com.umutyenidil.atlas.dto.response.VariantOptionValueResponseDTO;
import com.umutyenidil.atlas.entity.VariantOption;
import com.umutyenidil.atlas.entity.VariantOptionValue;
import com.umutyenidil.atlas.exception.ConflictException;
import com.umutyenidil.atlas.exception.ValidationException;
import com.umutyenidil.atlas.repository.VariantOptionRepository;
import com.umutyenidil.atlas.repository.VariantOptionValueRepository;
import com.umutyenidil.atlas.service.VariantOptionValueMapper;
import com.umutyenidil.atlas.service.VariantOptionValueService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DefaultVariantOptionValueService implements VariantOptionValueService {

    private final VariantOptionRepository variantOptionRepository;
    private final VariantOptionValueRepository variantOptionValueRepository;
    private final VariantOptionValueMapper variantOptionValueMapper;

    public DefaultVariantOptionValueService(VariantOptionRepository variantOptionRepository, VariantOptionValueRepository variantOptionValueRepository, VariantOptionValueMapper variantOptionValueMapper) {
        this.variantOptionRepository = variantOptionRepository;
        this.variantOptionValueRepository = variantOptionValueRepository;
        this.variantOptionValueMapper = variantOptionValueMapper;
    }

    @Override
    public VariantOptionValueResponseDTO createVariantOptionValue(VariantOptionValueCreateRequestDTO request) {
        variantOptionRepository
                .findById(UUID.fromString(request.variantOptionId()))
                .orElseThrow(() -> new ValidationException("variantOptionId", "exception.variant-option.notFound"));

        variantOptionValueRepository
                .findByVariantOption_IdAndName(UUID.fromString(request.variantOptionId()), request.name())
                .ifPresent((variantOptionValue) -> {
                    throw new ConflictException("VARIANT_OPTION_VALUE", "exception.variant-option-value.alreadyExists");
                });

        var variantOptionValue = variantOptionValueRepository
                .save(
                        VariantOptionValue.builder()
                                .variantOption(
                                        VariantOption.builder()
                                                .id(UUID.fromString(request.variantOptionId()))
                                                .build()
                                )
                                .name(request.name())
                                .build()
                );


        return variantOptionValueMapper.toResponse(variantOptionValue);
    }
}
