package com.example.HPPO_Backend.repository;

import java.util.List;

import com.example.HPPO_Backend.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.description = ?1")
    List<Category> findByName(String description);

    Page<Category> findByType(Category.CategoryType type, Pageable pageable);

}
