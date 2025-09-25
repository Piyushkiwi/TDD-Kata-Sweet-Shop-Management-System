package com.sweetmanagement.backend.controllerTest;

import com.sweetmanagement.backend.entity.Sweet;
import com.sweetmanagement.backend.repository.SweetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SweetControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SweetRepository sweetRepository;

    private Sweet sweet;

    @BeforeEach
    void setUp() {
        sweetRepository.deleteAll();

        sweet = new Sweet();
        sweet.setName("Gulab Jamun");
        sweet.setCategory("Traditional");
        sweet.setPrice(2.5);
        sweet.setQuantity(100);
        sweetRepository.save(sweet);
    }

    @Test
    void testGetAllSweets() {
        ResponseEntity<Sweet[]> response = restTemplate.withBasicAuth("user","password")
                .getForEntity("/api/sweets", Sweet[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].getName()).isEqualTo("Gulab Jamun");
    }

    @Test
    void testCreateSweet() {
        Sweet newSweet = new Sweet();
        newSweet.setName("Rasmalai");
        newSweet.setCategory("Dairy");
        newSweet.setPrice(4.0);
        newSweet.setQuantity(50);

        ResponseEntity<Sweet> response = restTemplate.withBasicAuth("user","password")
                .postForEntity("/api/sweets", newSweet, Sweet.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Rasmalai");
    }

    @Test
    void testDeleteSweet() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin","password");
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/sweets/" + sweet.getId(),
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(sweetRepository.findById(sweet.getId())).isEmpty();
    }
}
