package com.example.HPPO_Backend.controllers.admin;

import com.example.HPPO_Backend.entity.CarouselItem;
import com.example.HPPO_Backend.entity.dto.CarouselItemResponse;
import com.example.HPPO_Backend.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/carousel")
public class AdminCarouselController {
    
    @Autowired
    private CarouselService carouselService;
    
    @GetMapping
    public ResponseEntity<List<CarouselItemResponse>> getAllCarouselItems() {
        List<CarouselItem> items = carouselService.getAllCarouselItems();
        List<CarouselItemResponse> responses = items.stream()
                .map(CarouselItemResponse::fromCarouselItem)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addProductToCarousel(@PathVariable Long productId) {
        try {
            CarouselItem item = carouselService.addProductToCarousel(productId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CarouselItemResponse.fromCarouselItem(item));
        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getReason() != null ? e.getReason() : "Error", 
                                "message", e.getReason() != null ? e.getReason() : "Error al agregar producto al carrusel"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor", "message", e.getMessage()));
        }
    }
    
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeProductFromCarousel(@PathVariable Long productId) {
        try {
            carouselService.removeProductFromCarousel(productId);
            return ResponseEntity.noContent().build();
        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getReason() != null ? e.getReason() : "Error", 
                                "message", e.getReason() != null ? e.getReason() : "Error al eliminar producto del carrusel"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor", "message", e.getMessage()));
        }
    }
    
    @PutMapping("/reorder")
    public ResponseEntity<?> reorderCarouselItems(@RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> carouselItemIds = request.get("carouselItemIds");
            if (carouselItemIds == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "carouselItemIds es requerido"));
            }
            carouselService.reorderCarouselItems(carouselItemIds);
            return ResponseEntity.ok(Map.of("message", "Carrusel reordenado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor", "message", e.getMessage()));
        }
    }
    
    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> checkProductInCarousel(@PathVariable Long productId) {
        boolean isInCarousel = carouselService.isProductInCarousel(productId);
        return ResponseEntity.ok(Map.of("isInCarousel", isInCarousel));
    }
    
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getCarouselItemCount() {
        long count = carouselService.getCarouselItemCount();
        return ResponseEntity.ok(Map.of("count", count));
    }
}
