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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SweetControllerTest {

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
    private Sweet existingSweet;

    @BeforeEach
    void setUp() {
        // 1. Clean all data to ensure a fresh start for each test
        sweetRepository.deleteAll();
        userRepository.deleteAll();

        // 2. Create and save a regular user and an admin user
        User regularUser = new User();
        regularUser.setName("user");
        regularUser.setEmail("user@example.com");
        regularUser.setPassword(passwordEncoder.encode("password"));
        regularUser.setRole(Role.USER);
        userRepository.save(regularUser);

        User adminUser = new User();
        adminUser.setName("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("password"));
        adminUser.setRole(Role.ADMIN);
        userRepository.save(adminUser);

        // 3. Log in both users to get their unique JWTs
        this.userJwtToken = loginAndGetToken("user@example.com", "password");
        this.adminJwtToken = loginAndGetToken("admin@example.com", "password");

        // 4. Create a sample sweet in the database for GET, PUT, DELETE tests
        existingSweet = new Sweet(null, "Gulab Jamun", "Traditional", 2.5, 100);
        sweetRepository.save(existingSweet);
    }

    // Helper method to log in a user and return their token
    private String loginAndGetToken(String email, String password) {
        User loginRequest = new User();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);
        // A successful login returns the token directly as a String
        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login", loginRequest, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    @Test
    void testGetAllSweets_withValidToken_shouldSucceed() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userJwtToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange("/api/sweets", HttpMethod.GET, requestEntity, List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void testCreateSweet_withValidToken_shouldSucceed() {
        // The request body is the Sweet entity itself, as per your design
        Sweet newSweet = new Sweet(null, "Rasmalai", "Dairy", 4.0, 50);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userJwtToken);
        HttpEntity<Sweet> requestEntity = new HttpEntity<>(newSweet, headers);

        ResponseEntity<Sweet> response = restTemplate.postForEntity("/api/sweets", requestEntity, Sweet.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Rasmalai");
    }

    @Test
    void testDeleteSweet_asAdmin_shouldSucceed() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminJwtToken); // Use ADMIN token
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange("/api/sweets/" + existingSweet.getId(), HttpMethod.DELETE, requestEntity, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(sweetRepository.findById(existingSweet.getId())).isEmpty();
    }

    @Test
    void testGetAllSweets_withoutToken_shouldBeUnauthorized() {
        // No Authorization header is set
        HttpEntity<Void> requestEntity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<String> response = restTemplate.exchange("/api/sweets", HttpMethod.GET, requestEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}