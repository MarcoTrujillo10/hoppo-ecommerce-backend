package com.example.HPPO_Backend.entity.dto;

import com.example.HPPO_Backend.entity.Product;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private Double price;
    private Double discountedPrice;
    private Double discountAmount;
    private Integer discount;
    private boolean hasDiscount;
    private String description;
    private Integer stock;
    private BrandResponse brand;
    private CategoryResponse category;
    private List<ProductImageResponse> images;

    public static ProductResponse fromProduct(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setDiscountedPrice(product.getDiscountedPrice());
        response.setDiscountAmount(product.getDiscountAmount());
        response.setDiscount(product.getDiscount());
        response.setHasDiscount(product.hasDiscount());
        response.setDescription(product.getDescription());
        response.setStock(product.getStock());
        
        if (product.getBrand() != null) {
            response.setBrand(BrandResponse.fromBrand(product.getBrand()));
        }
        
        if (product.getCategory() != null) {
            response.setCategory(CategoryResponse.fromCategory(product.getCategory()));
        }
        
        if (product.getImages() != null) {
            response.setImages(product.getImages().stream()
                    .map(ProductImageResponse::fromProductImage)
                    .collect(Collectors.toList()));
        }
        
        return response;
    }
}
