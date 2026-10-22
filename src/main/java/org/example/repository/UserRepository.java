package org.example.repository;

import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    List<User> findAllByActiveTrue();

    Optional<User> findByIdAndActiveTrue(UUID id);

    Optional<User> findByEmailAndActiveTrue(String email);
}
