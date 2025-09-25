package com.sweetmanagement.backend.serviceTest;

import com.sweetmanagement.backend.entity.Sweet;
import com.sweetmanagement.backend.repository.SweetRepository;
import com.sweetmanagement.backend.service.InventoryService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional // Roll back database changes after each test
class InventoryServiceIntegrationTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private SweetRepository sweetRepository;

    private Sweet testSweet;

    @BeforeEach
    void setUp() {
        sweetRepository.deleteAll();
        // Create a sweet with a known quantity before each test
        testSweet = new Sweet(null, "Ladoo", "Festival", 1.0, 50);
        testSweet = sweetRepository.save(testSweet);
    }

    @Test
    void whenPurchaseSweet_withSufficientStock_thenQuantityDecreases() {
        // Act
        Sweet updatedSweet = inventoryService.purchaseSweet(testSweet.getId(), 10);

        // Assert
        assertThat(updatedSweet.getQuantity()).isEqualTo(40);

        // Verify against the database
        Sweet sweetFromDb = sweetRepository.findById(testSweet.getId()).get();
        assertThat(sweetFromDb.getQuantity()).isEqualTo(40);
    }

    @Test
    void whenPurchaseSweet_withInsufficientStock_thenThrowException() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            inventoryService.purchaseSweet(testSweet.getId(), 60); // Try to buy more than available
        });

        assertThat(exception.getMessage()).contains("Not enough stock");

        // Verify quantity in DB remains unchanged
        Sweet sweetFromDb = sweetRepository.findById(testSweet.getId()).get();
        assertThat(sweetFromDb.getQuantity()).isEqualTo(50);
    }

    @Test
    void whenPurchaseSweet_thatDoesNotExist_thenThrowException() {
        assertThrows(EntityNotFoundException.class, () -> {
            inventoryService.purchaseSweet(999L, 1);
        });
    }

    @Test
    void whenRestockSweet_thenQuantityIncreases() {
        // Act
        Sweet updatedSweet = inventoryService.restockSweet(testSweet.getId(), 100);

        // Assert
        assertThat(updatedSweet.getQuantity()).isEqualTo(150);

        // Verify against the database
        Sweet sweetFromDb = sweetRepository.findById(testSweet.getId()).get();
        assertThat(sweetFromDb.getQuantity()).isEqualTo(150);
    }

    @Test
    void whenRestockSweet_thatDoesNotExist_thenThrowException() {
        assertThrows(EntityNotFoundException.class, () -> {
            inventoryService.restockSweet(999L, 100);
        });
    }
}