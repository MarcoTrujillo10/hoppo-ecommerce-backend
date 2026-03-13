package com.example.HPPO_Backend.repository;

import com.example.HPPO_Backend.entity.CartProduct;
import com.example.HPPO_Backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {
    List<CartProduct> findByCartUserId(Long userId);
    List<CartProduct> findByProduct(Product product);
}
