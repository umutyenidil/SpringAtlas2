package com.umutyenidil.atlas.dto.response;

import com.umutyenidil.atlas.enumeration.StorageProvider;
import lombok.Builder;

@Builder
public record ProductImageUploadResponseDTO(
        String fileKey,
        String url,
        String cdnUri,
        StorageProvider provider
) {
}
