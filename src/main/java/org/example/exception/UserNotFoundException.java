package org.example.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID id) {
        super("Utilisateur introuvable: " + id);
    }
}
