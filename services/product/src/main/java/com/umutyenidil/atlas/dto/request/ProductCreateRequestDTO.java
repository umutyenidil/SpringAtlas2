package com.umutyenidil.atlas.dto.request;

import com.umutyenidil.atlas.annotation.dto.request.productcreaterequestdto.requiredproductimages.RequiredProductImages;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@RequiredProductImages
public record ProductCreateRequestDTO(
        @NotBlank(message = "{validation.product.name.notblank}")
        @NotNull(message = "{validation.product.name.notnull}")
        String name,

        @NotBlank(message = "{validation.product.description.notblank}")
        String description,

        @NotBlank(message = "{validation.product.brand.notblank}")
        @Size(min = 2, max = 50, message = "{validation.product.brand.size}")
        String brand,

        @NotBlank(message = "{validation.product.category.notblank}")
        @Size(min = 2, max = 50, message = "{validation.product.category.size}")
        String category,

        @Valid
        List<ImageCreateRequestDTO> images,

        @Valid
        @NotEmpty(message = "{validation.product.variants.notempty}")
        List<VariantCreateRequestDTO> variants
) {
}
