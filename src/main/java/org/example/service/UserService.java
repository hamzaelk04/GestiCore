package org.example.service;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User create(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        return repository.save(new User(name, email));
    }

    @Transactional(readOnly = true)
    public User get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public List<User> list() {
        return repository.findAll();
    }
}
