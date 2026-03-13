package com.example.HPPO_Backend.entity.dto;

import com.example.HPPO_Backend.entity.Category;
import lombok.Data;

@Data
public class CategoryResponse {
    private Long id;
    private String description;
    private String type;

    public static CategoryResponse fromCategory(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setDescription(category.getDescription());
        response.setType(category.getType().toString());
        return response;
    }
}
