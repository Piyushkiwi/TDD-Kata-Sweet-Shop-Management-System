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

import java.util.HashMap;
import java.util.Map;

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

        // Create normal user
        User user = new User();
        user.setName("user");
        user.setEmail("user@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(Role.USER);
        userRepository.save(user);

        // Create admin user
        User admin = new User();
        admin.setName("admin");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        // Get JWT tokens
        this.userJwtToken = loginAndGetToken("user@example.com", "password");
        this.adminJwtToken = loginAndGetToken("admin@example.com", "password");

        // Add a sweet
        sweet = new Sweet(null, "Ladoo", "Traditional", 10.0, 20);
        sweetRepository.save(sweet);
    }

    private String loginAndGetToken(String email, String password) {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", email);
        loginRequest.put("password", password);

        ResponseEntity<Map> response = restTemplate.postForEntity("/api/auth/login", loginRequest, Map.class);

        return (String) response.getBody().get("token"); // expects { "token": "..." }
    }

    @Test
    void testPurchaseSweet_asUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userJwtToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Sweet> response = restTemplate.exchange(
                "/api/sweets/" + sweet.getId() + "/purchase?quantity=5",
                HttpMethod.POST,
                request,
                Sweet.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getQuantity()).isEqualTo(15); // 20 - 5
    }

    @Test
    void testRestockSweet_asAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminJwtToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Sweet> response = restTemplate.exchange(
                "/api/sweets/" + sweet.getId() + "/restock?quantity=10",
                HttpMethod.POST,
                request,
                Sweet.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getQuantity()).isEqualTo(30); // 20 + 10
    }

    @Test
    void testRestockSweet_asUser_forbidden() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userJwtToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/sweets/" + sweet.getId() + "/restock?quantity=10",
                HttpMethod.POST,
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
