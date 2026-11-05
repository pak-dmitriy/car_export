package com.export.car.config;

import com.export.car.entity.Role;
import com.export.car.entity.User;
import com.export.car.entity.UserStatus;
import com.export.car.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.demo-buyer.email}")
    private String buyerEmail;

    @Value("${app.demo-buyer.password}")
    private String buyerPassword;

    @Override
    public void run(ApplicationArguments args) {
        createIfConfigured("Admin", adminEmail, adminPassword, Role.ADMIN);
        createIfConfigured("Test Buyer", buyerEmail, buyerPassword, Role.USER);
    }

    private void createIfConfigured(String fullName, String email, String password, Role role) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return;
        }
        if (password.length() < 8) {
            throw new IllegalStateException("Password for " + role + " must be at least 8 characters");
        }

        String normalizedEmail = email.trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            return;
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        log.info("Created user {} with role {}", normalizedEmail, role);
    }
}