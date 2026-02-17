package com.umutyenidil.atlas.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {
    private final String subject;

    public UnauthorizedException(String subject, String message) {
        this.subject = subject;
        super(message);
    }
}
