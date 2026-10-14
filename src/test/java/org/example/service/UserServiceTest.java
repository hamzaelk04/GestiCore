package org.example.service;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @Test
    void create_shouldSaveUser() {
        when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = service.create("Alice", "alice@mail.com");

        assertEquals("Alice", result.getName());
        verify(repository).save(any(User.class));
    }

    @Test
    void create_shouldFail_whenNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> service.create(" ", "a@b.c"));
        verifyNoInteractions(repository);
    }
}