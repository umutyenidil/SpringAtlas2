package com.umutyenidil.atlas.dto.response;

import lombok.Builder;

@Builder
public record ErrorDetailDTO(
        Type type,
        String subject,
        String message
) {
    public enum Type {
        SERVER,
        VALIDATION,
        CLIENT,
        AUTH,
        CONFLICT,
        NOT_FOUND
    }
}