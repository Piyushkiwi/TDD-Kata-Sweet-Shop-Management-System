package com.sweetmanagement.backend.repository;

import com.sweetmanagement.backend.entity.Sweet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SweetRepository extends JpaRepository<Sweet,Long> {
}
