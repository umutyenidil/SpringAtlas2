package com.umutyenidil.atlas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public final class UnauthorizedException extends LocalizedException {
    public UnauthorizedException(String subject, String messageKey, Object... args) {
        super(subject, messageKey, args);
    }
}
