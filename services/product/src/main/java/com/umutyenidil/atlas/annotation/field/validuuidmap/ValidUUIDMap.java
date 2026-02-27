package com.umutyenidil.atlas.annotation.field.validuuidmap;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidUUIDMapValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUUIDMap {
    String message() default "The map contains invalid UUID format.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
