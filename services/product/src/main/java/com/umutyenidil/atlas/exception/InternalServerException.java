package com.umutyenidil.atlas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public final class InternalServerException extends LocalizedException {
    public InternalServerException(String subject, String messageKey, Object... args) {
        super(subject, messageKey, args);
    }
}
