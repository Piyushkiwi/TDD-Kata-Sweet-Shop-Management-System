package com.sweetmanagement.backend.service;

//import com.sweetmanagement.backend.controller.Sweet;
import com.sweetmanagement.backend.entity.Sweet;
import com.sweetmanagement.backend.repository.SweetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
public class SweetService {

    @Autowired
    private SweetRepository sweetRepository;

    //Creates a new sweet. (POST /api/sweets)

    @Transactional
    public Sweet createSweet(Sweet sweetRequest) {
        Sweet newSweet = new Sweet();
        newSweet.setName(sweetRequest.getName());
        newSweet.setCategory(sweetRequest.getCategory());
        newSweet.setPrice(sweetRequest.getPrice());
        newSweet.setQuantity(sweetRequest.getQuantity());
        return sweetRepository.save(newSweet);
    }

    //Retrieves all sweets. (GET /api/sweets)

    public List<Sweet> getAllSweets() {
        return sweetRepository.findAll();
    }

    //Updates an existing sweet. (PUT /api/sweets/:id)
    @Transactional
    public Sweet updateSweet(Long id, Sweet sweetRequest) {
        // Find the existing sweet or throw an exception
        Sweet existingSweet = sweetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sweet not found with id: " + id));

        // Update the fields
        existingSweet.setName(sweetRequest.getName());
        existingSweet.setCategory(sweetRequest.getCategory());
        existingSweet.setPrice(sweetRequest.getPrice());
        existingSweet.setQuantity(sweetRequest.getQuantity());

        // Save the updated entity
        return sweetRepository.save(existingSweet);
    }

    //Deletes a sweet by its ID. (DELETE /api/sweets/:id)
    public void deleteSweet(Long id) {
        if (!sweetRepository.existsById(id)) {
            throw new EntityNotFoundException("Sweet not found with id: " + id);
        }
        sweetRepository.deleteById(id);
    }

    //Searches for sweets using various criteria. (GET /api/sweets/search)
    public List<Sweet> searchSweets(String name, String category,Double minPrice, Double maxPrice) {
        return sweetRepository.searchSweets(name, category, minPrice, maxPrice);
    }
}