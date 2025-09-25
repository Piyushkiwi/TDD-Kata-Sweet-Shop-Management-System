package com.sweetmanagement.backend.repository;

import com.sweetmanagement.backend.entity.Sweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface SweetRepository extends JpaRepository<Sweet,Long> {
    /* Searches for sweets based on optional criteria.
       The query dynamically builds the WHERE clause based on non-null parameters.
    */
    @Query("SELECT s FROM Sweet s WHERE " +
            "(:name IS NULL OR lower(s.name) LIKE lower(concat('%', :name, '%'))) AND " +
            "(:category IS NULL OR lower(s.category) = lower(:category))) AND " +
            "(:minPrice IS NULL OR s.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR s.price <= :maxPrice)")
    List<Sweet> searchSweets(
            @Param("name") String name,
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );
}
