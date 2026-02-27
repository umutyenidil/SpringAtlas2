package com.umutyenidil.atlas.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public final class MultipleValidationException extends RuntimeException {

    private final List<Item> items;

    public MultipleValidationException(List<Item> items) {
        this.items = items;
    }

    public record Item(
            String subject,
            String messageKey,
            Object... args
    ) {
    }
}
