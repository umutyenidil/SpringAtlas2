package com.umutyenidil.atlas.dto.response;

import lombok.Builder;

@Builder
public record JWTResponseDTO(
        String accessToken,
        String refreshToken
) {
}
