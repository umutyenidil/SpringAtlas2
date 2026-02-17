package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.entity.Auth;
import com.umutyenidil.atlas.exception.NotFoundException;
import com.umutyenidil.atlas.repository.AuthRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultUserDetailsServiceTest {

    @Mock
    private AuthRepository authRepository;

    @InjectMocks
    private DefaultUserDetailsService userDetailsService;

    @Test
    @DisplayName("Should return UserDetails when user exists")
    void loadUserByUsername_WhenUserExists_ShouldReturnUser() {
        // Arrange
        String email = "test@test.com";
        String password = "password";
        Auth auth = Auth.builder()
                .email(email)
                .password(password)
                .build();

        when(authRepository.findByEmail(email)).thenReturn(Optional.of(auth));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertEquals(password, result.getPassword());

        verify(authRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Should throw NotFoundException with correct key when user does not exists")
    void loadUserByUsername_WhenUserDoesNotExists_ShouldThrowNotFoundException() {
        // Arrange
        String email = "test@test.com";
        when(authRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        assertEquals("USER", exception.getSubject());
        assertEquals("exception.auth.user.not.found", exception.getMessageKey());

        assertNotNull(exception.getArgs());
        assertEquals(email, exception.getArgs()[0]);

        verify(authRepository).findByEmail(email);
    }
}
