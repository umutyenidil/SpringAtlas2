package com.umutyenidil.atlas.dto.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.List;

@Getter
@SuperBuilder(builderMethodName = "errorBuilder")
public class ErrorResponseDTO extends BaseResponseDTO {
    private List<ErrorDetailDTO> errors;

    public static ErrorResponseDTO of(List<ErrorDetailDTO> errors) {
        return ErrorResponseDTO.errorBuilder()
                .success(false)
                .timestamp(Instant.now())
                .errors(errors)
                .build();
    }

    public static ErrorResponseDTO of(ErrorDetailDTO error) {
        return of(List.of(error));
    }

    public static ErrorResponseDTO of(ErrorDetailDTO.Type type, String subject, String message) {
        return of(ErrorDetailDTO.builder()
                .type(type)
                .subject(subject)
                .message(message)
                .build());
    }
}