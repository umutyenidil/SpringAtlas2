package com.umutyenidil.atlas;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailTemplate {

    WELCOME("email/welcome");

    private final String path;
}
