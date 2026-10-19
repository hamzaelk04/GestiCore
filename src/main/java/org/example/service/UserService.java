package org.example.service;

import org.example.dto.PasswordChangeRequest;
import org.example.dto.UserCreateRequest;
import org.example.dto.UserResponse;
import org.example.dto.UserUpdateRequest;
import org.example.exception.AccessDeniedException;
import org.example.exception.BadRequestException;
import org.example.exception.EmailAlreadyUsedException;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.enums.Role;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse create(User currentUser, UserCreateRequest request) {
        requireAdmin(currentUser);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyUsedException("Cet email est déjà utilisé : " + request.getEmail());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll(User currentUser) {
        requireAdmin(currentUser);
        return userMapper.toResponseList(userRepository.findAllByActiveTrue());
    }

    @Transactional(readOnly = true)
    public UserResponse findById(User currentUser, UUID id) {
        requireAdmin(currentUser);
        return userMapper.toResponse(getActiveUser(id));
    }

    public UserResponse update(User currentUser, UUID id, UserUpdateRequest request) {
        requireAdmin(currentUser);

        User user = getActiveUser(id);
        checkEmailAvailable(request.getEmail(), id);

        userMapper.updateEntity(request, user);
        return userMapper.toResponse(userRepository.save(user));
    }

    public void deactivate(User currentUser, UUID id) {
        requireAdmin(currentUser);

        User user = getActiveUser(id);
        user.setActive(false); // soft delete
        userRepository.save(user);
    }


    @Transactional(readOnly = true)
    public UserResponse getMe(User currentUser) {
        return userMapper.toResponse(getActiveUser(currentUser.getId()));
    }

    public UserResponse updateMe(User currentUser, UserUpdateRequest request) {
        User user = getActiveUser(currentUser.getId());

        // A USER cannot change their own role
        if (request.getRole() != null && request.getRole() != user.getRole()) {
            throw new AccessDeniedException("Vous ne pouvez pas modifier votre rôle");
        }

        checkEmailAvailable(request.getEmail(), user.getId());

        userMapper.updateEntity(request, user);
        return userMapper.toResponse(userRepository.save(user));
    }

    // Optional feature
    public void changePassword(User currentUser, PasswordChangeRequest request) {
        User user = getActiveUser(currentUser.getId());

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Le mot de passe actuel est incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // ===================== Private helpers =====================

    private void requireAdmin(User currentUser) {
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Opération réservée au rôle ADMIN");
        }
    }

    private User getActiveUser(UUID id) {
        return userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable avec l'id " + id));
    }

    private void checkEmailAvailable(String email, UUID userId) {
        if (userRepository.existsByEmailAndIdNot(email, userId)) {
            throw new EmailAlreadyUsedException("Cet email est déjà utilisé : " + email);
        }
    }
}