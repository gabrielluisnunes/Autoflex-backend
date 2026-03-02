package com.autoflex.backend.dto.auth;

public record RegisterResponse(
        Long id,
        String username,
        String role) {
}
