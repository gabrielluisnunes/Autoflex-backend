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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("admin", "admin123");
        User user = User.builder()
                .id(1L)
                .username("admin")
                .password("hashed")
                .role(Role.ADMIN)
                .build();

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any())).thenReturn("jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(3600000L);

        LoginResponse response = authService.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertEquals("jwt-token", response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals("admin", response.username());
        assertEquals("ADMIN", response.role());
        assertEquals(3600L, response.expiresIn());
    }

    @Test
    void shouldThrowWhenLoginCredentialsAreInvalid() {
        LoginRequest request = new LoginRequest("admin", "wrong");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    @SuppressWarnings("null")
    void shouldRegisterUserWithDefaultRoleAndHashedPassword() {
        RegisterRequest request = new RegisterRequest("new-user", "user123");

        when(userRepository.existsByUsername("new-user")).thenReturn(false);
        when(passwordEncoder.encode("user123")).thenReturn("hashed-password");
        when(userRepository.save(argThat(Objects::nonNull))).thenAnswer(invocation -> {
            User arg = invocation.getArgument(0);
            arg.setId(10L);
            return arg;
        });

        RegisterResponse response = authService.register(request);

        assertEquals(10L, response.id());
        assertEquals("new-user", response.username());
        assertEquals("USER", response.role());
        verify(passwordEncoder).encode("user123");
    }

    @Test
    void shouldRejectRegisterWhenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest("admin", "admin123");
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () -> authService.register(request));

        assertTrue(exception.getMessage().contains("Username already exists"));
    }
}
