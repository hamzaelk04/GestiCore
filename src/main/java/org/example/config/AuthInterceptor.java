package org.example.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AuthInterceptor implements HandlerInterceptor {
    public static final String CURRENT_USER = "currentUser";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthInterceptor(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic ")) {
//            throw new UnauthorizedException("Authentification requise");
        }

        String decoded = "";
        try {
            decoded = new String(Base64.getDecoder().decode(header.substring(6)), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
//            throw new UnauthorizedException("En-tête Authorization invalide");
        }

        String[] parts = decoded.split(":", 2);
        if (parts.length != 2) {
//            throw new UnauthorizedException("Identifiants invalides");
        }

//        User user = userRepository.findByEmailAndActiveTrue(parts[0])
//                .orElseThrow(() -> new UnauthorizedException("Identifiants invalides"));

        User user = userRepository.findByEmailAndActiveTrue(parts[0])
                .orElseThrow(() -> new IllegalArgumentException("Identifiants invalides"));

        if (!passwordEncoder.matches(parts[1], user.getPassword())) {
//            throw new UnauthorizedException("Identifiants invalides");
        }

        request.setAttribute(CURRENT_USER, user);
        return true;
    }
}
