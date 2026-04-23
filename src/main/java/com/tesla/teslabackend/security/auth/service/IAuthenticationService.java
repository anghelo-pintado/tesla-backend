package com.tesla.gamification.security.auth.service;

import com.tesla.gamification.security.auth.dto.AuthenticationRequest;
import com.tesla.gamification.security.auth.dto.AuthenticationResponse;
import com.tesla.gamification.security.auth.dto.RegisterRequest;

public interface IAuthenticationService {

    AuthenticationResponse register(RegisterRequest registerRequest);
    AuthenticationResponse authenticate(AuthenticationRequest request);
    AuthenticationResponse refresh(String refreshToken);
}
