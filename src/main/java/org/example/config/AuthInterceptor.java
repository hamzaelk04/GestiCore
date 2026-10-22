package org.example.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.exception.AccessDeniedException;
import org.example.exception.UnauthorizedException;
import org.example.model.User;
import org.example.model.enums.Role;
import org.example.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AuthInterceptor implements HandlerInterceptor {

    public static final String CURRENT_USER = "currentUser";

    private static final String BASIC_PREFIX = "Basic ";
    private static final String SELF_PATH = "/api/users/me";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthInterceptor(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BASIC_PREFIX)) {
            throw new UnauthorizedException("Authentification requise");
        }

        String decoded;
        try {
            decoded = new String(Base64.getDecoder().decode(header.substring(BASIC_PREFIX.length()).trim()),
                    StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("En-tête Authorization invalide");
        }

        int separator = decoded.indexOf(':');
        if (separator < 0) {
            throw new UnauthorizedException("Identifiants invalides");
        }
        String email = decoded.substring(0, separator);
        String rawPassword = decoded.substring(separator + 1);

        User user = userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new UnauthorizedException("Identifiants invalides"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UnauthorizedException("Identifiants invalides");
        }

        if (user.getRole() != Role.ADMIN && !isSelfEndpoint(request)) {
            throw new AccessDeniedException("Opération réservée au rôle ADMIN");
        }

        request.setAttribute(CURRENT_USER, user);
        return true;
    }

    private boolean isSelfEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return path.equals(SELF_PATH) || path.startsWith(SELF_PATH + "/");
    }
}
