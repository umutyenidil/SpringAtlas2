package com.umutyenidil.atlas.dto.request;

import com.umutyenidil.atlas.annotation.field.validuuidmap.ValidUUIDMap;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record VariantCreateRequestDTO(
        @NotBlank(message = "{validation.variant.barcode.notblank}")
        String barcode,

        @NotNull(message = "{validation.variant.price.notnull}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{validation.variant.price.min}")
        BigDecimal price,

        @NotNull(message = "{validation.variant.stock.notnull}")
        @Min(value = 0, message = "{validation.variant.stock.min}")
        Integer stockQuantity,

        @Valid
        List<ImageCreateRequestDTO> images,

        @Size(min = 1, message = "{validation.variant.attributes.size}")
        @ValidUUIDMap(message = "{validation.constraints.uuid_map.invalid}")
        Map<String, String> attributes
) {
}
