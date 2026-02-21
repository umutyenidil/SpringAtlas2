package com.umutyenidil.atlas.service.impl;

import com.umutyenidil.atlas.exception.NotFoundException;
import com.umutyenidil.atlas.repository.AuthRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class DefaultUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;

    public DefaultUserDetailsService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) throws NotFoundException {
        return authRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("USER", "exception.auth.user.not.found", username));
    }
}
