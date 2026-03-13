package com.example.HPPO_Backend.service;

import com.example.HPPO_Backend.entity.Category;

import java.util.Optional;

import com.example.HPPO_Backend.entity.Product;
import com.example.HPPO_Backend.entity.dto.CategoryRequest;
import com.example.HPPO_Backend.entity.dto.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CategoryService {
     Page<Category> getCategories(PageRequest pageRequest, String type);
     Optional<Category> getCategoryById(Long categoryId);
     Category createCategory(String description, Category.CategoryType type) throws Exception;
     void deleteCategory(Long categoryId) throws Exception;
     Category updateCategory(Long categoryId, CategoryRequest categoryRequest);
}
