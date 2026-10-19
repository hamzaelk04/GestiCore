package org.example.repository;

import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    // Email uniqueness check on creation
    boolean existsByEmail(String email);

    // Email uniqueness check on update (ignores the user being updated)
    boolean existsByEmailAndIdNot(String email, UUID id);

    // Standard list: active users only
    List<User> findAllByActiveTrue();

    // One active user by id (deactivated users count as "not found")
    Optional<User> findByIdAndActiveTrue(UUID id);

    // Used by AuthInterceptor to log the user in
    Optional<User> findByEmailAndActiveTrue(String email);
}