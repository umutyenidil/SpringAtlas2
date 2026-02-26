package com.umutyenidil.atlas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record VariantOptionValueCreateRequestDTO(
        @NotBlank(message = "{validation.variant-option.id.notblank}")
        @NotNull(message = "{validation.variant-option.id.notnull}")
        @Pattern(
                regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                message = "{validation.variant-option.id.notnull}"
        )
        String variantOptionId,

        @NotBlank(message = "{validation.variant-option.name.notblank}")
        @NotNull(message = "{validation.variant-option.name.notnull}")
        String name
) {
}
