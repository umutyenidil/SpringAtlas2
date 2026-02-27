package com.umutyenidil.atlas.dto.response;

import lombok.Builder;

import java.util.Optional;

@Builder
public record ProductResponseDTO(
        String id,
        String name,
        Optional<String> description
) {
}
