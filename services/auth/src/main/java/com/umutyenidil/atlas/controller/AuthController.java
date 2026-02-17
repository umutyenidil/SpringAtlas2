package com.umutyenidil.atlas.controller;

import com.umutyenidil.atlas.dto.request.RegisterRequestDTO;
import com.umutyenidil.atlas.dto.response.JWTResponseDTO;
import com.umutyenidil.atlas.dto.response.SuccessResponseDTO;
import com.umutyenidil.atlas.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<SuccessResponseDTO<JWTResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO request) {
        JWTResponseDTO result = authService.register(request);

        return ResponseEntity.ok(SuccessResponseDTO.of(result));
    }
}
