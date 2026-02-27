package com.umutyenidil.atlas.dto.response;

import lombok.Builder;

@Builder
public record VariantOptionValueResponseDTO(
        String id,
        String variantOptionId,
        String name
) {
}
