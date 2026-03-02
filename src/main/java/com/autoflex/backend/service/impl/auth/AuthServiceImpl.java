package com.autoflex.backend.service.impl.auth;

import com.autoflex.backend.dto.auth.LoginRequest;
import com.autoflex.backend.dto.auth.LoginResponse;
import com.autoflex.backend.dto.auth.RegisterRequest;
import com.autoflex.backend.dto.auth.RegisterResponse;
import com.autoflex.backend.entity.Role;
import com.autoflex.backend.entity.User;
import com.autoflex.backend.exception.ConflictException;
import com.autoflex.backend.repository.UserRepository;
import com.autoflex.backend.security.JwtService;
import com.autoflex.backend.security.UserPrincipal;
import com.autoflex.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        String token = jwtService.generateToken(new UserPrincipal(user));

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs() / 1000,
                user.getUsername(),
                user.getRole().name());
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already exists: " + request.username());
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        User saved = userRepository.save(Objects.requireNonNull(user));

        return new RegisterResponse(saved.getId(), saved.getUsername(), saved.getRole().name());
    }
}
