package com.sweetmanagement.backend.controllerTest;

import com.sweetmanagement.backend.entity.Role;
import com.sweetmanagement.backend.entity.User;
import com.sweetmanagement.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // 2. INJECT THE PASSWORD ENCODER

    private User existingUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        existingUser = new User();
        existingUser.setName("Dipak");
        existingUser.setEmail("dipak@example.com");

        // 3. ENCODE THE PASSWORD BEFORE SAVING
        existingUser.setPassword(passwordEncoder.encode("password"));

        existingUser.setRole(Role.USER);

        userRepository.save(existingUser);
    }

    @Test
    void testRegisterNewUser() {
        // ... this test remains the same
        User newUser = new User();
        newUser.setName("Piyush");
        newUser.setEmail("piyush@example.com");
        newUser.setPassword("password123");
        newUser.setRole(Role.USER);

        ResponseEntity<User> response = restTemplate.postForEntity(
                "/api/auth/register", newUser, User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals("piyush@example.com", response.getBody().getEmail());
    }

    @Test
    void testLoginSuccess() {
        // ... this test remains the same
        User loginRequest = new User();
        loginRequest.setEmail("dipak@example.com");
        loginRequest.setPassword("password");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login", loginRequest, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody()); // JWT token
    }


    @Test
    void testLoginInvalidPassword() {
        User loginRequest = new User();
        loginRequest.setEmail("dipak@example.com");
        loginRequest.setPassword("wrongpassword");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login", loginRequest, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    void testLoginNonExistentUser() {
        User loginRequest = new User();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login", loginRequest, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }
}
