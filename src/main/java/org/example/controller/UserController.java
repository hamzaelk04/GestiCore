package org.example.controller;

import jakarta.validation.Valid;
import org.example.config.AuthInterceptor;
import org.example.dto.PasswordChangeRequest;
import org.example.dto.UserCreateRequest;
import org.example.dto.UserResponse;
import org.example.dto.UserUpdateRequest;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(
            @RequestAttribute(AuthInterceptor.CURRENT_USER) User currentUser,
            @Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(currentUser, request));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll(
            @RequestAttribute(AuthInterceptor.CURRENT_USER) User currentUser) {
        return ResponseEntity.ok(userService.findAll(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(
            @RequestAttribute(AuthInterceptor.CURRENT_USER) User currentUser,
            @PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(currentUser, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @RequestAttribute(AuthInterceptor.CURRENT_USER) User currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.update(currentUser, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @RequestAttribute(AuthInterceptor.CURRENT_USER) User currentUser,
            @PathVariable UUID id) {
        userService.deactivate(currentUser, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(
            @RequestAttribute(AuthInterceptor.CURRENT_USER) User currentUser) {
        return ResponseEntity.ok(userService.getMe(currentUser));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(
            @RequestAttribute(AuthInterceptor.CURRENT_USER) User currentUser,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateMe(currentUser, request));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @RequestAttribute(AuthInterceptor.CURRENT_USER) User currentUser,
            @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(currentUser, request);
        return ResponseEntity.ok().build();
    }
}
