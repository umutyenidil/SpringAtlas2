package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.dto.event.UserRegisteredEvent;
import com.umutyenidil.atlas.dto.request.RegisterRequestDTO;
import com.umutyenidil.atlas.dto.response.JWTResponseDTO;
import com.umutyenidil.atlas.entity.Auth;
import com.umutyenidil.atlas.exception.ConflictException;
import com.umutyenidil.atlas.messaging.publisher.UserEventPublisher;
import com.umutyenidil.atlas.repository.AuthRepository;
import com.umutyenidil.atlas.service.AuthService;
import com.umutyenidil.atlas.service.JWTService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class DefaultAuthService implements AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jWTService;
    private final UserEventPublisher userEventPublisher;

    public DefaultAuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder, JWTService jWTService, UserEventPublisher userEventPublisher) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jWTService = jWTService;
        this.userEventPublisher = userEventPublisher;
    }

    @Override
    public JWTResponseDTO register(RegisterRequestDTO request) {
        if (authRepository.findByEmail(request.email()).isPresent()) {
            throw new ConflictException("EMAIL", "exception.auth.email.conflict", request.email());
        }

        var auth = authRepository.save(
                Auth.builder()
                        .email(request.email())
                        .password(passwordEncoder.encode(request.password()))
                        .build()
        );

        var claims = new HashMap<String, Object>();

        var accessToken = jWTService.generateAccessToken(claims, auth);
        var refreshToken = jWTService.generateRefreshToken(claims, auth);

        userEventPublisher.publishUserRegisteredEvent(UserRegisteredEvent.builder()
                .email(auth.getEmail())
                .build());

        return JWTResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}