package com.umutyenidil.atlas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VariantOptionCreateRequestDTO(
        @NotBlank(message = "{validation.variant-option.name.notblank}")
        @NotNull(message = "{validation.variant-option.name.notnull}")
        String name
) {
}
