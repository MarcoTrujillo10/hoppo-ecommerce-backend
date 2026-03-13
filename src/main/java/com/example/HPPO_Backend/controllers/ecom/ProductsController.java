package com.example.HPPO_Backend.controllers.ecom;

import com.example.HPPO_Backend.entity.Product;
import com.example.HPPO_Backend.entity.dto.ProductRequest;
import com.example.HPPO_Backend.entity.dto.ProductResponse;
import com.example.HPPO_Backend.service.ProductService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("products")
public class ProductsController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "false") boolean includeOutOfStock) {

        PageRequest pageRequest = (page != null && size != null)
                ? PageRequest.of(page, size)
                : PageRequest.of(0, Integer.MAX_VALUE);

        Page<Product> products = productService.searchAndFilterProducts(name, minPrice, maxPrice, includeOutOfStock, pageRequest);
        Page<ProductResponse> productResponses = products.map(ProductResponse::fromProduct);
        return ResponseEntity.ok(productResponses);
    }

    @GetMapping({"/{productId}"})
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId) {
        Optional<Product> result = this.productService.getProductById(productId);
        return result.map(product -> ResponseEntity.ok(ProductResponse.fromProduct(product)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping
    public ResponseEntity<Object> createProduct(@Valid @RequestBody ProductRequest productRequest) throws Exception {
        Product result = this.productService.createProduct(productRequest);
        return ResponseEntity.created(URI.create("/products/" + result.getId())).body(ProductResponse.fromProduct(result));
    }
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductRequest productRequest) {
        Product updatedProduct = productService.updateProduct(productId, productRequest);
        return ResponseEntity.ok(ProductResponse.fromProduct(updatedProduct));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
