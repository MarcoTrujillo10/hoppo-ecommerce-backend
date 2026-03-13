package com.example.HPPO_Backend.repository;

import com.example.HPPO_Backend.entity.CarouselItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarouselItemRepository extends JpaRepository<CarouselItem, Long> {
    
    @Query("SELECT c FROM CarouselItem c LEFT JOIN FETCH c.product p LEFT JOIN FETCH p.images WHERE p.stock > 0 ORDER BY c.displayOrder ASC, c.createdAt ASC")
    List<CarouselItem> findActiveItemsOrdered();
    
    @Query("SELECT c FROM CarouselItem c LEFT JOIN FETCH c.product ORDER BY c.displayOrder ASC, c.createdAt ASC")
    List<CarouselItem> findAllOrdered();
    
    Optional<CarouselItem> findByProductId(Long productId);
    
    boolean existsByProductId(Long productId);
    
    long count();
}


