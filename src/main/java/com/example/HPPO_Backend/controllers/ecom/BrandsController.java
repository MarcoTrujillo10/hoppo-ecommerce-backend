package com.example.HPPO_Backend.controllers.ecom;

import com.example.HPPO_Backend.entity.Brand;
import com.example.HPPO_Backend.entity.Product;
import com.example.HPPO_Backend.entity.dto.BrandRequest;
import com.example.HPPO_Backend.service.BrandService;
import com.example.HPPO_Backend.service.BrandServiceImpl;
import com.example.HPPO_Backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("brands")
public class BrandsController {
    @Autowired
    private BrandService brandService ;

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Brand>> getBrands() {
        return ResponseEntity.ok(this.brandService.getBrands());
    }

    @GetMapping({"/{brandId}"})
    public ResponseEntity<Brand> getBrandById(@PathVariable Long brandId) {
        Optional<Brand> result = this.brandService.getBrandById(brandId);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping
    public ResponseEntity<Object> createBrand(@RequestBody BrandRequest brandRequest) throws Exception {
        Brand result = this.brandService.createBrand(brandRequest);
        return ResponseEntity.created(URI.create("/brands/" + result.getId())).body(result);
    }

    @DeleteMapping("/{brandId}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long brandId){
        this.brandService.deleteBrand(brandId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{brandId}/products")
    public ResponseEntity<Page<Product>> getProductsByBrand(
            @PathVariable Long brandId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        PageRequest pageRequest = (page != null && size != null)
                ? PageRequest.of(page, size)
                : PageRequest.of(0, Integer.MAX_VALUE);

        return ResponseEntity.ok(productService.getAvailableProductsByBrand(brandId, pageRequest));
    }
}
