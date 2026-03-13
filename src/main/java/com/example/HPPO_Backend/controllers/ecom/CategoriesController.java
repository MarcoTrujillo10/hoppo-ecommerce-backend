package com.example.HPPO_Backend.controllers.ecom;

import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.HPPO_Backend.entity.Category;
import com.example.HPPO_Backend.entity.Product;
import com.example.HPPO_Backend.entity.dto.CategoryRequest;
import com.example.HPPO_Backend.service.CategoryService;
import com.example.HPPO_Backend.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("categories")
public class CategoriesController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Page<Category>> getCategories(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String type) {
        if (page == null || size == null)
            return ResponseEntity.ok(categoryService.getCategories(PageRequest.of(0, Integer.MAX_VALUE), type));
        return ResponseEntity.ok(categoryService.getCategories(PageRequest.of(page, size), type));
    }

    @GetMapping({"/{categoryId}"})
    public ResponseEntity<Category> getCategoryById(@PathVariable Long categoryId) {
        Optional<Category> result = this.categoryService.getCategoryById(categoryId);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping
    public ResponseEntity<Object> createCategory(@RequestBody CategoryRequest categoryRequest) throws Exception {
        Category result = this.categoryService.createCategory(categoryRequest.getDescription(), categoryRequest.getType());
        return ResponseEntity.created(URI.create("/categories/" + result.getId())).body(result);
    }
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) throws Exception {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryRequest categoryRequest) throws Exception {
        Category updatedCategory = categoryService.updateCategory(categoryId, categoryRequest);
        return ResponseEntity.ok(updatedCategory);
    }

    @GetMapping("/{categoryId}/products")
    public ResponseEntity<Page<Product>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        PageRequest pageRequest = (page != null && size != null)
                ? PageRequest.of(page, size)
                : PageRequest.of(0, Integer.MAX_VALUE);

        return ResponseEntity.ok(productService.getAvailableProductsByCategory(categoryId, pageRequest));
    }


}
