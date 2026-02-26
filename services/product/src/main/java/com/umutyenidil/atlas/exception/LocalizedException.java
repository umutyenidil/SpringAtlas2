package com.umutyenidil.atlas.exception;

import lombok.Getter;

@Getter
public abstract sealed class LocalizedException extends RuntimeException permits NotFoundException, ConflictException, UnauthorizedException, ValidationException, InternalServerException{
    private final String subject;
    private final String messageKey;
    private final Object[] args;

    public LocalizedException(String subject, String messageKey, Object... args) {
        super(messageKey);
        this.subject = subject;
        this.messageKey = messageKey;
        this.args = args;
    }
}
