package com.example.HPPO_Backend.service;

import com.example.HPPO_Backend.entity.Category;
import com.example.HPPO_Backend.entity.dto.CategoryRequest;
import com.example.HPPO_Backend.exceptions.CategoryDuplicateException;
import com.example.HPPO_Backend.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public  class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Page<Category> getCategories(PageRequest pageable, String type) {
        if (type != null && !type.isEmpty()) {
            try {
                Category.CategoryType categoryType = Category.CategoryType.valueOf(type.toUpperCase());
                return categoryRepository.findByType(categoryType, pageable);
            } catch (IllegalArgumentException e) {
                // Si el tipo no es válido, devolver todas las categorías
                return categoryRepository.findAll(pageable);
            }
        }
        return categoryRepository.findAll(pageable);
    }

    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }

    public Category createCategory(String description, Category.CategoryType type) throws CategoryDuplicateException {
        List<Category> categories = categoryRepository.findByName(description);
        if (categories.isEmpty()) {
        return categoryRepository.save(new Category(description, type));
        }
        throw new CategoryDuplicateException();
    }
    public void deleteCategory(Long categoryId) throws Exception {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada con id: " + categoryId);
        }
        categoryRepository.deleteById(categoryId);

    }


    @Override
    public Category updateCategory(Long categoryId, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada con id: " + categoryId));
        category.setDescription(categoryRequest.getDescription());
        category.setType(categoryRequest.getType());
        return categoryRepository.save(category);
    }


}
