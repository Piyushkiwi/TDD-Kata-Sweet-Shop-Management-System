package com.sweetmanagement.backend.serviceTest;

import com.sweetmanagement.backend.entity.Sweet;
import com.sweetmanagement.backend.repository.SweetRepository;
import com.sweetmanagement.backend.service.SweetService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class SweetServiceTest {

    @Autowired
    private SweetService sweetService;

    @Autowired
    private SweetRepository sweetRepository;

    private Sweet sweet1;
    private Sweet sweet2;

    @BeforeEach
    void setUp() {
        sweetRepository.deleteAll();

        sweet1 = new Sweet();
        sweet1.setName("Gulab Jamun");
        sweet1.setCategory("Traditional");
        sweet1.setPrice(2.50);
        sweet1.setQuantity(100);

        sweet2 = new Sweet();
        sweet2.setName("Rasmalai");
        sweet2.setCategory("Dairy");
        sweet2.setPrice(4.00);
        sweet2.setQuantity(50);

        sweetRepository.save(sweet1);
        sweetRepository.save(sweet2);
    }

    @Test
    void testCreateSweet() {
        Sweet request = new Sweet();
        request.setName("Jalebi");
        request.setCategory("Fried");
        request.setPrice(1.50);
        request.setQuantity(200);

        Sweet createdSweet = sweetService.createSweet(request);

        assertThat(createdSweet).isNotNull();
        assertThat(createdSweet.getId()).isNotNull();
        assertThat(sweetRepository.count()).isEqualTo(3);
    }

    @Test
    void testGetAllSweets() {
        List<Sweet> sweets = sweetService.getAllSweets();
        assertThat(sweets).hasSize(2);
    }

    @Test
    void testUpdateSweet_Success() {
        Sweet updateRequest = new Sweet();
        updateRequest.setName("Royal Gulab Jamun");
        updateRequest.setPrice(3.00);
        updateRequest.setCategory("Premium");
        updateRequest.setQuantity(80);

        Sweet updatedSweet = sweetService.updateSweet(sweet1.getId(), updateRequest);

        assertThat(updatedSweet.getName()).isEqualTo("Royal Gulab Jamun");
        assertThat(updatedSweet.getPrice()).isEqualTo(3.00);
        assertThat(updatedSweet.getQuantity()).isEqualTo(80);
    }

    @Test
    void testUpdateSweet_NotFound() {
        Sweet request = new Sweet();
        assertThrows(EntityNotFoundException.class, () -> {
            sweetService.updateSweet(999L, request);
        });
    }

    @Test
    void testDeleteSweet_Success() {
        long countBefore = sweetRepository.count();
        sweetService.deleteSweet(sweet1.getId());
        long countAfter = sweetRepository.count();

        assertThat(countAfter).isEqualTo(countBefore - 1);
        assertThat(sweetRepository.findById(sweet1.getId())).isEmpty();
    }

    @Test
    void testDeleteSweet_NotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            sweetService.deleteSweet(999L);
        });
    }

    @Test
    void testSearchSweets_byName() {
        List<Sweet> results = sweetService.searchSweets("Jamun", null, null, null);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Gulab Jamun");
    }

    @Test
    void testSearchSweets_byCategory() {
        List<Sweet> results = sweetService.searchSweets(null, "Dairy", null, null);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Rasmalai");
    }

    @Test
    void testSearchSweets_byPriceRange() {
        List<Sweet> results = sweetService.searchSweets(null, null, 3.00, 5.00);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Rasmalai");
    }

    @Test
    void testSearchSweets_byNameAndPrice() {
        List<Sweet> results = sweetService.searchSweets("Rasmalai", null, 3.00, null);
        assertThat(results).hasSize(1);
    }

    @Test
    void testSearchSweets_noResults() {
        List<Sweet> results = sweetService.searchSweets("NonExistent", null, null, null);
        assertThat(results).isEmpty();
    }
}
