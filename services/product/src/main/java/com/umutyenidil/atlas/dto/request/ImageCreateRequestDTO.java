package com.umutyenidil.atlas.dto.request;

import com.umutyenidil.atlas.enumeration.StorageProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ImageCreateRequestDTO(
        @NotBlank(message = "{validation.image.filekey.notblank}")
        String fileKey,

        @NotNull(message = "{validation.image.provider.notnull}")
        StorageProvider provider
) {
}