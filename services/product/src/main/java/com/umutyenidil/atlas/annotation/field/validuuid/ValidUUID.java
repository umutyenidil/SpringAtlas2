package com.umutyenidil.atlas.annotation.field.validuuid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidUUIDValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUUID {

    String message() default "Please enter a valid UUID.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
