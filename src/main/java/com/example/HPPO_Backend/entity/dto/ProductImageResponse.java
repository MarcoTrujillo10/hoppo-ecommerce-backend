package com.example.HPPO_Backend.entity.dto;

import com.example.HPPO_Backend.entity.ProductImage;
import lombok.Data;

@Data
public class ProductImageResponse {
    private Long id;
    private String imageUrl;

    public static ProductImageResponse fromProductImage(ProductImage productImage) {
        ProductImageResponse response = new ProductImageResponse();
        response.setId(productImage.getId());
        response.setImageUrl(productImage.getImageUrl());
        return response;
    }
}
