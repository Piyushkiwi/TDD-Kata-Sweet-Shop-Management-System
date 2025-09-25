package com.sweetmanagement.backend.service;

import com.sweetmanagement.backend.entity.Sweet;
import com.sweetmanagement.backend.repository.SweetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    @Autowired
    private SweetRepository sweetRepository;

    @Transactional
    public Sweet purchaseSweet(Long sweetId, int quantityToPurchase) {
        Sweet sweet = sweetRepository.findById(sweetId)
                .orElseThrow(() -> new EntityNotFoundException("Sweet not found with id: " + sweetId));

        if (sweet.getQuantity() < quantityToPurchase) {
            throw new IllegalArgumentException("Not enough stock available for sweet: " + sweet.getName());
        }

        sweet.setQuantity(sweet.getQuantity() - quantityToPurchase);
        return sweetRepository.save(sweet);
    }

    @Transactional
    public Sweet restockSweet(Long sweetId, int quantityToRestock) {
        Sweet sweet = sweetRepository.findById(sweetId)
                .orElseThrow(() -> new EntityNotFoundException("Sweet not found with id: " + sweetId));

        sweet.setQuantity(sweet.getQuantity() + quantityToRestock);
        return sweetRepository.save(sweet);
    }
}