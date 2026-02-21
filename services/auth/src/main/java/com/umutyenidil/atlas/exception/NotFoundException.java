package com.umutyenidil.atlas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public final class NotFoundException extends LocalizedException {
    public NotFoundException(String subject, String messageKey, Object... args) {
        super(subject, messageKey, args);
    }
}
