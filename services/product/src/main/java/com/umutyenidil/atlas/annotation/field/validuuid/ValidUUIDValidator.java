package com.umutyenidil.atlas.annotation.field.validuuid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.UUID;

public class ValidUUIDValidator implements ConstraintValidator<ValidUUID, String> {
    @Override
    public boolean isValid(String val, ConstraintValidatorContext ctx) {
        if (val == null || val.isEmpty()) {
            return true;
        }

        try {
            UUID.fromString(val);
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }
}
