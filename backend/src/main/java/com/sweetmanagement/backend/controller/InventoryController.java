package com.sweetmanagement.backend.controller;

import com.sweetmanagement.backend.entity.Sweet;
import com.sweetmanagement.backend.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sweets/inventory")
@RequiredArgsConstructor
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // Purchase sweet (Any authenticated user)
    @PostMapping("/{id}/purchase")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Sweet> purchaseSweet(
            @PathVariable Long id,
            @RequestParam int quantity) {
        Sweet updatedSweet = inventoryService.purchaseSweet(id, quantity);
        return ResponseEntity.ok(updatedSweet);
    }

    // Restock sweet (Admin only)
    @PostMapping("/{id}/restock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Sweet> restockSweet(
            @PathVariable Long id,
            @RequestParam int quantity) {
        Sweet updatedSweet = inventoryService.restockSweet(id, quantity);
        return ResponseEntity.ok(updatedSweet);
    }
}
