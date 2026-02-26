package com.umutyenidil.atlas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public final class ValidationException extends LocalizedException {
    public ValidationException(String subject, String messageKey, Object... args) {
        super(subject, messageKey, args);
    }
}
