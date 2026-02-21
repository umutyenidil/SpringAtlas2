package com.umutyenidil.atlas.service;

import com.umutyenidil.atlas.dto.request.RegisterRequestDTO;
import com.umutyenidil.atlas.dto.response.JWTResponseDTO;

public interface AuthService {
    JWTResponseDTO register(RegisterRequestDTO request);
}
