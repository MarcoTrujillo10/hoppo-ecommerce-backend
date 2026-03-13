package com.example.HPPO_Backend.repository;

import com.example.HPPO_Backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.name = ?1")
    List<Product> findByName(String name);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> getProductsByBrand(Long brandId, Pageable pageable);
    
    
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.stock > 0")
    Page<Product> getAvailableProductsByCategory(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    Page<Product> getAllProductsByCategory(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.brand.id = :brandId AND p.stock > 0")
    Page<Product> getAvailableProductsByBrand(@Param("brandId") Long brandId, Pageable pageable);
    
    

    Page<Product> getProductByCategory(Long categoryId, PageRequest pageRequest);
    @Query("SELECT p FROM Product p WHERE " +
            "(p.stock >= :minStock) AND " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> searchAndFilterProductsWithMinStock(
            @Param("name") String name,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minStock") int minStock,
            Pageable pageable);
}
