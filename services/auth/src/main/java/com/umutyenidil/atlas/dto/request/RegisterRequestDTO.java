package com.umutyenidil.atlas.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank(message = "{validation.auth.email.notblank}")
        @Email(message = "{validation.auth.email.invalid}")
        String email,

        @NotBlank(message = "{validation.auth.password.notblank}")
        @Size(min = 8, message = "{validation.auth.password.size}")
        String password
) {
}
