package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.dto.request.RegisterRequestDTO;
import com.umutyenidil.atlas.dto.response.JWTResponseDTO;
import com.umutyenidil.atlas.entity.Auth;
import com.umutyenidil.atlas.exception.ConflictException;
import com.umutyenidil.atlas.repository.AuthRepository;
import com.umutyenidil.atlas.service.JWTService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultAuthServiceTest {

    @Mock
    private AuthRepository authRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JWTService jwtService;
    @InjectMocks
    private DefaultAuthService authService;

    private static final String MOCK_ENCODED_PASSWORD = "mock-encoded-password";
    private static final String MOCK_ACCESS_TOKEN = "mock-access-token";
    private static final String MOCK_REFRESH_TOKEN = "mock-refresh-token";
    private static RegisterRequestDTO request;
    private static Auth auth;

    @BeforeAll
    static void setUp() {
        request = new RegisterRequestDTO("test@test.com", "testtest");

        auth = Auth.builder()
                .id(UUID.randomUUID())
                .email(request.email())
                .password(MOCK_ENCODED_PASSWORD)
                .build();
    }

    @Test
    @DisplayName("Should register successfully when email is unique")
    void register_WhenEmailIsUnique_ShouldReturnTokens() {
        // Arrange
        when(authRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        when(passwordEncoder.encode(request.password())).thenReturn(MOCK_ENCODED_PASSWORD);

        when(authRepository.save(any(Auth.class))).thenReturn(auth);

        when(jwtService.generateAccessToken(anyMap(), eq(auth))).thenReturn(MOCK_ACCESS_TOKEN);

        when(jwtService.generateRefreshToken(anyMap(), eq(auth))).thenReturn(MOCK_REFRESH_TOKEN);

        JWTResponseDTO response = authService.register(request);

        assertNotNull(response);
        assertEquals(MOCK_ACCESS_TOKEN, response.accessToken());
        assertEquals(MOCK_REFRESH_TOKEN, response.refreshToken());

        verify(authRepository, times(1)).save(any(Auth.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when email is already exists")
    void register_WhenEmailIsAlreadyExists_ShouldThrowConflictException() {
        // Arrange
        when(authRepository.findByEmail(request.email())).thenReturn(Optional.of(auth));

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            authService.register(request);
        });

        assertEquals("EMAIL", exception.getSubject());
        assertEquals("exception.auth.email.conflict", exception.getMessageKey());
        assertEquals(request.email(), exception.getArgs()[0]);

        verify(authRepository, never()).save(any(Auth.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(jwtService, never()).generateAccessToken(anyMap(), any(Auth.class));
        verify(jwtService, never()).generateRefreshToken(anyMap(), any(Auth.class));
    }
}
