package com.autoflex.backend.service;

import com.autoflex.backend.dto.auth.LoginRequest;
import com.autoflex.backend.dto.auth.LoginResponse;
import com.autoflex.backend.dto.auth.RegisterRequest;
import com.autoflex.backend.dto.auth.RegisterResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    RegisterResponse register(RegisterRequest request);
}
