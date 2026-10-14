package org.example.repository;

import org.example.config.RootConfig;
import org.example.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig(RootConfig.class)
@Transactional   // rollback automatique après chaque test
class UserRepositoryIT {

    @Autowired
    private UserRepository repository;

    @Test
    void shouldPersistAndFindUser() {
        User saved = repository.save(new User("Bob", "bob@mail.com"));

        assertNotNull(saved.getId());
        assertEquals("Bob", repository.findById(saved.getId()).orElseThrow().getName());

        verify(repository).save(saved);
    }

    @Test
    void shouldListUsers() {
        repository.save(new User("Carl", "carl@mail.com"));
        assertFalse(repository.findAll().isEmpty());
    }
}