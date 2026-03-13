package com.example.HPPO_Backend.service;
 
import com.example.HPPO_Backend.entity.Product;
import com.example.HPPO_Backend.entity.dto.ProductRequest;
import com.example.HPPO_Backend.exceptions.ProductDuplicateException;
 
import java.util.List;
import java.util.Optional;
 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ProductService {
    Page<Product> getProducts(PageRequest pageRequest);
    Optional<Product> getProductById(Long productId);
    Product createProduct(ProductRequest productRequest) throws ProductDuplicateException;

    void deleteProduct(Long productId);
    Product updateProduct(Long productId, ProductRequest productRequest);
    Page<Product> getProductsByCategory(Long categoryId, PageRequest pageRequest);
    Page<Product> getProductsByBrand(Long brandId, PageRequest pageRequest);;
    Page<Product> searchAndFilterProducts(String name, Double minPrice, Double maxPrice, boolean includeOutOfStock, PageRequest pageRequest);
    Page<Product> getAvailableProductsByCategory(Long categoryId, PageRequest pageRequest);
    Page<Product> getAvailableProductsByBrand(Long brandId, PageRequest pageRequest);
}