package com.umutyenidil.atlas.dto.response;

import lombok.Builder;

@Builder
public record VariantOptionResponseDTO(
        String id,
        String name
) {
}
