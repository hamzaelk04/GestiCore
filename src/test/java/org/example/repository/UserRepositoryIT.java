package org.example.repository;

import org.example.config.DataInitializer;
import org.example.config.RootConfig;
import org.example.model.User;
import org.example.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@Transactional
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    private User activeUser;
    private User inactiveUser;

    @BeforeEach
    void setUp() {
        activeUser = userRepository.saveAndFlush(buildUser("active@it.com", true));
        inactiveUser = userRepository.saveAndFlush(buildUser("inactive@it.com", false));
    }

    @Test
    void save_generatesIdAndAuditDates() {
        assertNotNull(activeUser.getId());
        assertNotNull(activeUser.getCreatedAt());
        assertNotNull(activeUser.getUpdatedAt());
    }

    @Test
    void existsByEmail_returnsTrueForExistingEmailOnly() {
        assertTrue(userRepository.existsByEmail("active@it.com"));
        assertFalse(userRepository.existsByEmail("unknown@it.com"));
    }

    @Test
    void existsByEmailAndIdNot_ignoresTheUserItself() {
        assertFalse(userRepository.existsByEmailAndIdNot("active@it.com", activeUser.getId()));
        assertTrue(userRepository.existsByEmailAndIdNot("active@it.com", inactiveUser.getId()));
        assertTrue(userRepository.existsByEmailAndIdNot("active@it.com", UUID.randomUUID()));
    }

    @Test
    void findAllByActiveTrue_excludesDeactivatedUsers() {
        assertTrue(userRepository.findAllByActiveTrue().stream()
                .anyMatch(user -> user.getEmail().equals("active@it.com")));
        assertFalse(userRepository.findAllByActiveTrue().stream()
                .anyMatch(user -> user.getEmail().equals("inactive@it.com")));
    }

    @Test
    void findByIdAndActiveTrue_returnsOnlyActiveUsers() {
        assertTrue(userRepository.findByIdAndActiveTrue(activeUser.getId()).isPresent());
        assertTrue(userRepository.findByIdAndActiveTrue(inactiveUser.getId()).isEmpty());
    }

    @Test
    void findByEmailAndActiveTrue_returnsOnlyActiveUsers() {
        assertTrue(userRepository.findByEmailAndActiveTrue("active@it.com").isPresent());
        assertTrue(userRepository.findByEmailAndActiveTrue("inactive@it.com").isEmpty());
    }

    @Test
    void dataInitializer_createsDefaultAdmin() {
        User admin = userRepository.findByEmailAndActiveTrue(DataInitializer.ADMIN_EMAIL).orElseThrow();
        assertEquals(Role.ADMIN, admin.getRole());
    }

    @Test
    void save_withDuplicateEmail_violatesUniqueConstraint() {
        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.saveAndFlush(buildUser("active@it.com", true)));
    }

    private User buildUser(String email, boolean active) {
        User user = new User();
        user.setName("IT User");
        user.setEmail(email);
        user.setPassword("HASHED");
        user.setRole(Role.USER);
        user.setActive(active);
        return user;
    }
}
