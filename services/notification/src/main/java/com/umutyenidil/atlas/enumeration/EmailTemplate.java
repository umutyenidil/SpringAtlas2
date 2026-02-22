package com.umutyenidil.atlas.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailTemplate {

    WELCOME("email/welcome");

    private final String path;
}
