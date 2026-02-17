package com.umutyenidil.atlas.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    private final String subject;

    public NotFoundException(String subject, String message) {
        this.subject = subject;
        super(message);
    }
}
