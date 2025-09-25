package com.sweetmanagement.backend.serviceTest;

import com.sweetmanagement.backend.entity.User;
import com.sweetmanagement.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        userRepository.deleteAll();

        user = new User("Dipak", "dipak@example.com", "password", false);
        user = userRepository.save(user); // Save user for testing
    }

    @Test
    void testRegisterUser() {
        User newUser = new User("Piyush", "piyush@example.com", "password123", false);
        User saved = userService.registerUser(newUser);

        assertNotNull(saved.getId());
        assertEquals("piyush@example.com", saved.getEmail());
    }

    @Test
    void testFindByEmail() {
        User found = userService.findByEmail("dipak@example.com");
        assertNotNull(found);
        assertEquals("Dipak", found.getName());
    }
}
