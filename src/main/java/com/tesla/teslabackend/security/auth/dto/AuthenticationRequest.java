package com.tesla.gamification.security.auth.dto;

public record AuthenticationRequest(
        String codigo,
        String password
) {
}
