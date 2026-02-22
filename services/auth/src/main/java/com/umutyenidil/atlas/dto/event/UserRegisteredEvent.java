package com.umutyenidil.atlas.dto.event;

import lombok.Builder;

@Builder
public record UserRegisteredEvent(
        String email
) {
}
