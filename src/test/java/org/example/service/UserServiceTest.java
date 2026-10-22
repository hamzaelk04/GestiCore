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
import org.example.model.User;
import org.example.model.enums.Role;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    private User admin;
    private User standardUser;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, new UserMapper(), passwordEncoder);
        admin = buildUser(UUID.randomUUID(), "admin@test.com", Role.ADMIN);
        standardUser = buildUser(UUID.randomUUID(), "user@test.com", Role.USER);
    }

    @Test
    void create_asAdmin_encodesPasswordAndSavesActiveUser() {
        UserCreateRequest request = createRequest("Alice", "alice@test.com", "secret123", Role.USER);
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("HASHED");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.create(admin, request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("HASHED", captor.getValue().getPassword());
        assertTrue(captor.getValue().isActive());
        assertEquals("alice@test.com", response.getEmail());
        assertEquals(Role.USER, response.getRole());
    }

    @Test
    void create_withoutRole_defaultsToUser() {
        UserCreateRequest request = createRequest("Bob", "bob@test.com", "secret123", null);
        when(userRepository.existsByEmail("bob@test.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("HASHED");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.create(admin, request);

        assertEquals(Role.USER, response.getRole());
    }

    @Test
    void create_asUser_isForbidden() {
        UserCreateRequest request = createRequest("Alice", "alice@test.com", "secret123", Role.USER);

        assertThrows(AccessDeniedException.class, () -> userService.create(standardUser, request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void create_withExistingEmail_throwsConflict() {
        UserCreateRequest request = createRequest("Alice", "user@test.com", "secret123", Role.USER);
        when(userRepository.existsByEmail("user@test.com")).thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class, () -> userService.create(admin, request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findAll_asAdmin_returnsOnlyActiveUsersFromRepository() {
        when(userRepository.findAllByActiveTrue()).thenReturn(List.of(admin, standardUser));

        List<UserResponse> result = userService.findAll(admin);

        assertEquals(2, result.size());
    }

    @Test
    void findAll_asUser_isForbidden() {
        assertThrows(AccessDeniedException.class, () -> userService.findAll(standardUser));

        verify(userRepository, never()).findAllByActiveTrue();
    }

    @Test
    void findById_asAdmin_returnsUser() {
        when(userRepository.findByIdAndActiveTrue(standardUser.getId())).thenReturn(Optional.of(standardUser));

        UserResponse response = userService.findById(admin, standardUser.getId());

        assertEquals(standardUser.getEmail(), response.getEmail());
    }

    @Test
    void findById_unknownOrInactive_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(admin, id));
    }

    @Test
    void findById_asUser_isForbidden() {
        assertThrows(AccessDeniedException.class,
                () -> userService.findById(standardUser, admin.getId()));
    }

    @Test
    void update_asAdmin_updatesFields() {
        UserUpdateRequest request = updateRequest("New Name", "new@test.com", Role.ADMIN);
        when(userRepository.findByIdAndActiveTrue(standardUser.getId())).thenReturn(Optional.of(standardUser));
        when(userRepository.existsByEmailAndIdNot("new@test.com", standardUser.getId())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.update(admin, standardUser.getId(), request);

        assertEquals("New Name", response.getName());
        assertEquals("new@test.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());
    }

    @Test
    void update_withEmailOfAnotherUser_throwsConflict() {
        UserUpdateRequest request = updateRequest("Name", "admin@test.com", null);
        when(userRepository.findByIdAndActiveTrue(standardUser.getId())).thenReturn(Optional.of(standardUser));
        when(userRepository.existsByEmailAndIdNot("admin@test.com", standardUser.getId())).thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class,
                () -> userService.update(admin, standardUser.getId(), request));
    }

    @Test
    void update_asUser_isForbidden() {
        UserUpdateRequest request = updateRequest("Name", "x@test.com", null);

        assertThrows(AccessDeniedException.class,
                () -> userService.update(standardUser, admin.getId(), request));
    }

    @Test
    void deactivate_asAdmin_setsActiveToFalse() {
        when(userRepository.findByIdAndActiveTrue(standardUser.getId())).thenReturn(Optional.of(standardUser));

        userService.deactivate(admin, standardUser.getId());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertFalse(captor.getValue().isActive());
    }

    @Test
    void deactivate_asUser_isForbidden() {
        assertThrows(AccessDeniedException.class,
                () -> userService.deactivate(standardUser, admin.getId()));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deactivate_unknownUser_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deactivate(admin, id));
    }

    @Test
    void getMe_returnsCurrentUserProfile() {
        when(userRepository.findByIdAndActiveTrue(standardUser.getId())).thenReturn(Optional.of(standardUser));

        UserResponse response = userService.getMe(standardUser);

        assertEquals(standardUser.getEmail(), response.getEmail());
    }

    @Test
    void updateMe_updatesOwnNameAndEmail() {
        UserUpdateRequest request = updateRequest("Renamed", "renamed@test.com", null);
        when(userRepository.findByIdAndActiveTrue(standardUser.getId())).thenReturn(Optional.of(standardUser));
        when(userRepository.existsByEmailAndIdNot("renamed@test.com", standardUser.getId())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.updateMe(standardUser, request);

        assertEquals("Renamed", response.getName());
        assertEquals(Role.USER, response.getRole());
    }

    @Test
    void updateMe_withDifferentRole_isForbidden() {
        UserUpdateRequest request = updateRequest("Name", "user@test.com", Role.ADMIN);
        when(userRepository.findByIdAndActiveTrue(standardUser.getId())).thenReturn(Optional.of(standardUser));

        assertThrows(AccessDeniedException.class, () -> userService.updateMe(standardUser, request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_withCorrectCurrentPassword_savesNewHash() {
        PasswordChangeRequest request = passwordRequest("oldPassword", "newPassword1");
        standardUser.setPassword("OLD_HASH");
        when(userRepository.findByIdAndActiveTrue(standardUser.getId())).thenReturn(Optional.of(standardUser));
        when(passwordEncoder.matches("oldPassword", "OLD_HASH")).thenReturn(true);
        when(passwordEncoder.encode("newPassword1")).thenReturn("NEW_HASH");

        userService.changePassword(standardUser, request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("NEW_HASH", captor.getValue().getPassword());
    }

    @Test
    void changePassword_withWrongCurrentPassword_throwsBadRequest() {
        PasswordChangeRequest request = passwordRequest("wrong", "newPassword1");
        standardUser.setPassword("OLD_HASH");
        when(userRepository.findByIdAndActiveTrue(standardUser.getId())).thenReturn(Optional.of(standardUser));
        when(passwordEncoder.matches("wrong", "OLD_HASH")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> userService.changePassword(standardUser, request));

        verify(userRepository, never()).save(any(User.class));
    }

    private User buildUser(UUID id, String email, Role role) {
        User user = new User();
        user.setId(id);
        user.setName("Name");
        user.setEmail(email);
        user.setPassword("HASH");
        user.setRole(role);
        user.setActive(true);
        return user;
    }

    private UserCreateRequest createRequest(String name, String email, String password, Role role) {
        UserCreateRequest request = new UserCreateRequest();
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        request.setRole(role);
        return request;
    }

    private UserUpdateRequest updateRequest(String name, String email, Role role) {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setName(name);
        request.setEmail(email);
        request.setRole(role);
        return request;
    }

    private PasswordChangeRequest passwordRequest(String current, String next) {
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword(current);
        request.setNewPassword(next);
        return request;
    }
}
