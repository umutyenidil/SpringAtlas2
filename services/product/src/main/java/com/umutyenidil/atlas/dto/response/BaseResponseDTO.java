package com.umutyenidil.atlas.dto.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@SuperBuilder
public class BaseResponseDTO {
    private boolean success;
    private Instant timestamp;
}