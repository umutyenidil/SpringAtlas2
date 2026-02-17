package com.umutyenidil.atlas.filter;

import com.umutyenidil.atlas.config.SecurityConfig;
import com.umutyenidil.atlas.exception.UnauthorizedException;
import com.umutyenidil.atlas.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver exceptionResolver;
    private final JWTService jWTService;
    private final UserDetailsService userDetailsService;

    public JWTFilter(HandlerExceptionResolver handlerExceptionResolver, JWTService jWTService, UserDetailsService userDetailsService) {
        this.exceptionResolver = handlerExceptionResolver;
        this.jWTService = jWTService;
        this.userDetailsService = userDetailsService;
    }

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    @NullMarked
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return Arrays.stream(SecurityConfig.WHITE_LIST_URLS)
                .anyMatch(p -> antPathMatcher.match(p, request.getServletPath()));
    }

    @Override
    @NullMarked
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            final String jwt;
            final String email;

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException(
                        "BEARER",
                        "Authorization header must start with Bearer"
                );
            }

            // todo: add black listing logic.

            jwt = authorizationHeader.substring(7);
            email = jWTService.extractEmail(jwt);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = userDetailsService.loadUserByUsername(email);

                if (jWTService.isTokenValid(jwt, userDetails)) {
                    var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails((new WebAuthenticationDetailsSource()).buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            exceptionResolver.resolveException(request, response, null, e);
        }
    }
}
