package com.sweetmanagement.backend.serviceTest;

import com.sweetmanagement.backend.entity.Role;
import com.sweetmanagement.backend.entity.User;
import com.sweetmanagement.backend.repository.UserRepository;
import com.sweetmanagement.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

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

        // Create and save a test user (Red phase: prepare failing test)
        user = new User();
        user.setName("Dipak");
        user.setEmail("dipak@example.com");
        user.setPassword("password");
        user.setRole(Role.USER);
        user = userRepository.save(user);
    }

    @Test
    void testRegisterUser() {
        // Create a new user
        User newUser = new User();
        newUser.setName("Piyush");
        newUser.setEmail("piyush@example.com");
        newUser.setPassword("password123");
        newUser.setRole(Role.USER);

        // Call the service method (Green phase: make test pass)
        User saved = userService.registerUser(newUser);

        // Assertions
        assertNotNull(saved.getId());
        assertEquals("piyush@example.com", saved.getEmail());
    }

    @Test
    void testFindByEmail() {
        // Call the service method
        User found = userService.findByEmail("dipak@example.com");

        // Assertions
        assertNotNull(found);
        assertEquals("Dipak", found.getName());
    }
}
