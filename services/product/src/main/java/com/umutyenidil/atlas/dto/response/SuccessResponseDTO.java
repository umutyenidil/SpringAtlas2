package com.umutyenidil.atlas.dto.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@SuperBuilder(builderMethodName = "successBuilder")
public class SuccessResponseDTO<T> extends BaseResponseDTO {
    private T data;

    public static <T> SuccessResponseDTO<T> of(T data) {
        return SuccessResponseDTO.<T>successBuilder()
                .success(true)
                .timestamp(Instant.now())
                .data(data)
                .build();
    }
}