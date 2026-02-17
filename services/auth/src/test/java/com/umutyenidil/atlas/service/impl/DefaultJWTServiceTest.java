package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.entity.Auth;
import com.umutyenidil.atlas.service.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultJWTServiceTest {

    private JWTService jwtService;

    private final String SECRET_KEY = "e196afba2535b71d7ba3d0199db0489e1b89498223b4daff6dfba71e27db62ab";
    private final long ACCESS_EXPIRATION = 1000 * 60 * 30;
    private final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24;
    private final UUID AUTH_ID = UUID.randomUUID();
    private final String AUTH_EMAIL = "test@test.com";
    private final String AUTH_PASSWORD = "password";

    @BeforeEach
    void setUp() {

        jwtService = new DefaultJWTService();

        ReflectionTestUtils.setField(jwtService, "jwtSecret", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", ACCESS_EXPIRATION);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", REFRESH_EXPIRATION);
    }

    private Auth getMockAuth() {
        return Auth.builder()
                .id(AUTH_ID)
                .email(AUTH_EMAIL)
                .password(AUTH_PASSWORD)
                .build();
    }

    @Test
    @DisplayName("Should generate a valid access token with correct subject")
    void generateAccessToken_ShouldReturnValidToken() {
        // Arrange
        Auth auth = getMockAuth();

        // Act
        String token = jwtService.generateAccessToken(new HashMap<>(), auth);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());

        String extractedEmail = jwtService.extractEmail(token);
        assertEquals(auth.getEmail(), extractedEmail);
    }

    @Test
    @DisplayName("Should generate a refresh token with longer expiration")
    void generateRefreshToken_ShouldReturnValidToken() {
        // Arrange
        Auth auth = getMockAuth();

        // Act
        String token = jwtService.generateRefreshToken(new HashMap<>(), auth);

        // Assert
        assertNotNull(token);

        Date expiration = jwtService.extractExpiration(token);
        Date now = new Date();
        long diff = expiration.getTime() - now.getTime();

        assertTrue(diff > ACCESS_EXPIRATION);
    }

    @Test
    @DisplayName("Should extract correct email from token")
    void extractEmail_ShouldReturnCorrectEmail() {
        // Arrange
        Auth auth = getMockAuth();
        String token = jwtService.generateAccessToken(new HashMap<>(), auth);

        // Act
        String email = jwtService.extractEmail(token);

        // Assert
        assertEquals(AUTH_EMAIL, email);
    }

    @Test
    @DisplayName("Should return true when token is valid and user matches")
    void isTokenValid_ShouldReturnTrue_WhenUserMatches() {
        // Arrange
        Auth auth = getMockAuth();
        String token = jwtService.generateAccessToken(new HashMap<>(), auth);

        UserDetails userDetails = new User(auth.getEmail(), auth.getPassword(), new ArrayList<>());

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should return false when token belongs to another user")
    void isTokenValid_ShouldReturnFalse_WhenUserDoesNotMatch() {
        // Arrange
        Auth auth = getMockAuth();
        String token = jwtService.generateAccessToken(new HashMap<>(), auth);

        UserDetails anotherUser = new User("test2@test.com", "password2", new ArrayList<>());

        // Act
        boolean isValid = jwtService.isTokenValid(token, anotherUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return false when token is expired")
    void isTokenValid_ShouldReturnFalse_WhenTokenExpired() throws InterruptedException {
        // Arrange
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", 1L);
        Auth auth = getMockAuth();
        String token = jwtService.generateAccessToken(new HashMap<>(), auth);

        Thread.sleep(10);
        UserDetails userDetails = new User(auth.getEmail(), auth.getPassword(), new ArrayList<>());

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertFalse(isValid);
    }
}
