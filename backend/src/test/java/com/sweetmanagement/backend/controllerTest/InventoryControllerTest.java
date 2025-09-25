package com.sweetmanagement.backend.controllerTest;

import com.sweetmanagement.backend.entity.Role;
import com.sweetmanagement.backend.entity.Sweet;
import com.sweetmanagement.backend.entity.User;
import com.sweetmanagement.backend.repository.SweetRepository;
import com.sweetmanagement.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private SweetRepository sweetRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String userJwtToken;
    private String adminJwtToken;
    private Sweet sweet;

    @BeforeEach
    void setUp() {
        sweetRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setName("user");
        user.setEmail("user@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(Role.USER);
        userRepository.save(user);

        User admin = new User();
        admin.setName("admin");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        this.userJwtToken = loginAndGetToken("user@example.com", "password");
        this.adminJwtToken = loginAndGetToken("admin@example.com", "password");

        sweet = new Sweet(null, "Ladoo", "Traditional", 10.0, 20);
        sweet = sweetRepository.save(sweet);
    }

    // This is the corrected helper method for logging in
    private String loginAndGetToken(String email, String password) {
        User loginRequest = new User();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login", loginRequest, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    @Test
    void testPurchaseSweet_asUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userJwtToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // NOTE: The URL now includes "/inventory" to match your controller
        ResponseEntity<Sweet> response = restTemplate.postForEntity(
                "/api/sweets/inventory/{id}/purchase?quantity=5",
                request,
                Sweet.class,
                sweet.getId()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getQuantity()).isEqualTo(15); // 20 - 5
    }

    @Test
    void testRestockSweet_asAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminJwtToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // NOTE: The URL now includes "/inventory"
        ResponseEntity<Sweet> response = restTemplate.postForEntity(
                "/api/sweets/inventory/{id}/restock?quantity=10",
                request,
                Sweet.class,
                sweet.getId()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getQuantity()).isEqualTo(30); // 20 + 10
    }
}