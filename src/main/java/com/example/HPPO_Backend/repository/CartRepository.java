package com.example.HPPO_Backend.repository;

import com.example.HPPO_Backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
    
    @Query("SELECT c FROM Cart c WHERE c.expiresAt < :currentTime")
    List<Cart> findExpiredCarts(@Param("currentTime") LocalDateTime currentTime);
    
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.expiresAt < :currentTime")
    void deleteExpiredCarts(@Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId AND c.expiresAt > :currentTime")
    Optional<Cart> findActiveCartByUserId(@Param("userId") Long userId, @Param("currentTime") LocalDateTime currentTime);
}
