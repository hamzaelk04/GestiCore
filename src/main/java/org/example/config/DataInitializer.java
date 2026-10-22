package org.example.config;

import jakarta.annotation.PostConstruct;
import org.example.model.User;
import org.example.model.enums.Role;
import org.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    public static final String ADMIN_EMAIL = "admin@example.com";
    public static final String ADMIN_PASSWORD = "admin1234";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (!userRepository.existsByEmail(ADMIN_EMAIL)) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail(ADMIN_EMAIL);
            admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);
        }
    }
}
