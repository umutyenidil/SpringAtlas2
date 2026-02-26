package com.umutyenidil.atlas.annotation.field.validuuidmap;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;
import java.util.UUID;

public class ValidUUIDMapValidator implements ConstraintValidator<ValidUUIDMap, Map<String, String>> {

    @Override
    public boolean isValid(Map<String, String> map, ConstraintValidatorContext ctx) {
        if (map == null || map.isEmpty()) {
            return true;
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!isValidUUID(entry.getKey()) || !isValidUUID(entry.getValue())) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidUUID(String uuidString) {
        if (uuidString == null) {
            return false;
        }

        try {
            UUID.fromString(uuidString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
