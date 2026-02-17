package com.umutyenidil.atlas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public final class ConflictException extends LocalizedException {
    public ConflictException(String subject, String messageKey, Object... args) {
        super(subject, messageKey, args);
    }
}
