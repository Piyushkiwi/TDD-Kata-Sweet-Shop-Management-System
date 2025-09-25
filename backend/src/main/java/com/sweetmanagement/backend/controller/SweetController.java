package com.sweetmanagement.backend.controller;

import com.sweetmanagement.backend.entity.Sweet;
import com.sweetmanagement.backend.service.SweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sweets")
@RequiredArgsConstructor
public class SweetController {

    @Autowired
    private  SweetService sweetService;

    // Create a new sweet
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Sweet> createSweet(@RequestBody Sweet newSweet) { // Now accepts Sweet entity
        Sweet createdSweet = sweetService.createSweet(newSweet);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSweet);
    }

    // Get all sweets
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Sweet>> getAllSweets() {
        List<Sweet> sweets = sweetService.getAllSweets();
        return ResponseEntity.ok(sweets);
    }

    // Search sweets
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Sweet>> searchSweets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        List<Sweet> sweets = sweetService.searchSweets(name, category, minPrice, maxPrice);
        return ResponseEntity.ok(sweets);
    }

    // Update sweet
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Sweet> updateSweet(@PathVariable Long id, @RequestBody Sweet sweetDetails) { // Now accepts Sweet entity
        Sweet updatedSweet = sweetService.updateSweet(id, sweetDetails);
        return ResponseEntity.ok(updatedSweet);
    }

    // Delete sweet (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSweet(@PathVariable Long id) {
        sweetService.deleteSweet(id);
        return ResponseEntity.noContent().build();
    }
}