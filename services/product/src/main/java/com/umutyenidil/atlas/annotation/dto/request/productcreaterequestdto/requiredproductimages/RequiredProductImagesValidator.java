package com.umutyenidil.atlas.annotation.dto.request.productcreaterequestdto.requiredproductimages;

import com.umutyenidil.atlas.dto.request.ProductCreateRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequiredProductImagesValidator implements ConstraintValidator<RequiredProductImages, ProductCreateRequestDTO> {

    @Override
    public boolean isValid(ProductCreateRequestDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null || dto.variants() == null || dto.variants().isEmpty()) {
            return true;
        }

        boolean mainProductHasImage = dto.images() != null && !dto.images().isEmpty();

        boolean anyVariantHasImage = dto.variants().stream()
                .anyMatch(v -> v.images() != null && !v.images().isEmpty());

        boolean allVariantHasImage = dto.variants().stream()
                .allMatch(v -> v.images() != null && !v.images().isEmpty());

        if (anyVariantHasImage) {

            if (mainProductHasImage) {
                buildViolation(ctx, "images", "{validation.product.images.forbidden_when_variants_have_images}");
                return false;
            }

            if (!allVariantHasImage) {
                for (int i = 0; i < dto.variants().size(); i++) {
                    var variantImages = dto.variants().get(i).images();

                    if (variantImages == null || variantImages.isEmpty()) {
                        buildViolation(ctx, "variants[" + i + "].images", "{validation.variant.images.required_because_others_have}");
                        return false;
                    }
                }
            }
        } else {
            if(!mainProductHasImage) {
                buildViolation(ctx, "images", "{validation.product.images.required_when_no_variant_images}");
                return false;
            }
        }

        return true;
    }

    private void buildViolation(ConstraintValidatorContext ctx, String fieldName, String messageTemplate) {
        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(messageTemplate)
                .addPropertyNode(fieldName)
                .addConstraintViolation();
    }
}
