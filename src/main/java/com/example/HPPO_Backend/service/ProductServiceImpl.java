package com.example.HPPO_Backend.service;

import com.example.HPPO_Backend.entity.*;
import com.example.HPPO_Backend.entity.dto.ProductRequest;
import com.example.HPPO_Backend.exceptions.ProductDuplicateException;
import com.example.HPPO_Backend.repository.BrandRepository;
import com.example.HPPO_Backend.repository.CartProductRepository;
import com.example.HPPO_Backend.repository.CategoryRepository;
import com.example.HPPO_Backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CartProductRepository cartProductRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProducts(PageRequest pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long productId) {
        return productRepository.findById(productId);
    }

    @Override
    public Product createProduct(ProductRequest productRequest) throws ProductDuplicateException {

        if (!productRepository.findByName(productRequest.getName()).isEmpty()) {
            throw new ProductDuplicateException();
        }


        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada con id: " + productRequest.getCategoryId()));
        Brand brand = brandRepository.findById(productRequest.getBrandId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Marca no encontrada con id: " + productRequest.getBrandId()));


        Product product = new Product();
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setDescription(productRequest.getDescription());
        product.setStock(productRequest.getStock());
        product.setBrand(brand);
        product.setCategory(category);
        product.setDiscount(productRequest.getDiscount());

        if (productRequest.getImageUrls() != null && !productRequest.getImageUrls().isEmpty()) {
            List<ProductImage> images = productRequest.getImageUrls().stream()
                    .map(url -> new ProductImage(url, product))
                    .collect(Collectors.toList());
            product.setImages(images);
        }

        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long productId, ProductRequest productRequest) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + productId));


        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada con id: " + productRequest.getCategoryId()));
        Brand brand = brandRepository.findById(productRequest.getBrandId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Marca no encontrada con id: " + productRequest.getBrandId()));


        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setDescription(productRequest.getDescription());
        product.setStock(productRequest.getStock());
        product.setBrand(brand);
        product.setCategory(category);
        product.setDiscount(productRequest.getDiscount());

        if (product.getImages() != null) {
            
            product.getImages().clear();
        }
        if (productRequest.getImageUrls() != null && !productRequest.getImageUrls().isEmpty()) {
            List<ProductImage> newImages = productRequest.getImageUrls().stream()
                    .map(url -> new ProductImage(url, product))
                    .collect(Collectors.toList());
            product.getImages().addAll(newImages);
        }

        return productRepository.save(product);
    }




    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + productId));
        
        // Eliminar todos los CartProduct que referencian este producto
        List<CartProduct> cartProducts = cartProductRepository.findByProduct(product);
        cartProductRepository.deleteAll(cartProducts);
        
        // Ahora eliminar el producto
        productRepository.deleteById(productId);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(Long categoryId, PageRequest pageRequest) {
        return productRepository.getProductByCategory(categoryId, pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsByBrand(Long brandId, PageRequest pageRequest) {
        return productRepository.getProductsByBrand(brandId, pageRequest);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchAndFilterProducts(String name, Double minPrice, Double maxPrice, boolean includeOutOfStock, PageRequest pageRequest) {
        // Si includeOutOfStock es true, minStock = 0 (incluye todos)
        // Si includeOutOfStock es false, minStock = 1 (solo con stock > 0)
        int minStock = includeOutOfStock ? 0 : 1;
        return productRepository.searchAndFilterProductsWithMinStock(name, minPrice, maxPrice, minStock, (Pageable) pageRequest);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Product> getAvailableProductsByCategory(Long categoryId, PageRequest pageRequest) {
        return productRepository.getAvailableProductsByCategory(categoryId, pageRequest);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<Product> getAvailableProductsByBrand(Long brandId, PageRequest pageRequest) {
        return productRepository.getAvailableProductsByBrand(brandId, pageRequest);
    }
}