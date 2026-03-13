package com.example.HPPO_Backend.entity.dto;

import com.example.HPPO_Backend.entity.Category.CategoryType;
import lombok.Data;

@Data
public class CategoryRequest {
    private int id;
    private String description;
    private CategoryType type;
}
