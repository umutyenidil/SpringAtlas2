package com.umutyenidil.atlas.service;

import com.umutyenidil.atlas.entity.Auth;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;

public interface JWTService {
    String generateToken(Auth auth);

    String generateAccessToken(Map<String, Object> claims, Auth auth);

    String generateRefreshToken(Map<String, Object> claims, Auth auth);

    boolean isTokenValid(String token, UserDetails userDetails);

    String extractEmail(String token);

    Date extractExpiration(String token);
}
