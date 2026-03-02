package com.autoflex.backend.config;

import com.autoflex.backend.entity.Role;
import com.autoflex.backend.entity.User;
import com.autoflex.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DefaultUserSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${autoflex.security.seed-default-users:true}")
    private boolean seedDefaultUsers;

    @Override
    public void run(ApplicationArguments args) {
        if (!seedDefaultUsers) {
            return;
        }

        createIfMissing("admin", "admin123", Role.ADMIN);
        createIfMissing("user", "user123", Role.USER);
    }

    private void createIfMissing(String username, String rawPassword, Role role) {
        if (userRepository.existsByUsername(username)) {
            return;
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();

        userRepository.save(Objects.requireNonNull(user));
    }
}
