package com.example.HPPO_Backend.service;

import com.example.HPPO_Backend.entity.CarouselItem;
import com.example.HPPO_Backend.entity.Product;
import com.example.HPPO_Backend.repository.CarouselItemRepository;
import com.example.HPPO_Backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CarouselServiceImpl implements CarouselService {
    
    private static final int MAX_CAROUSEL_ITEMS = 5;
    
    @Autowired
    private CarouselItemRepository carouselItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<CarouselItem> getActiveCarouselItems() {
        return carouselItemRepository.findActiveItemsOrdered();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CarouselItem> getAllCarouselItems() {
        return carouselItemRepository.findAllOrdered();
    }
    
    @Override
    @Transactional
    public CarouselItem addProductToCarousel(Long productId) {
        // Verificar que el producto existe
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Producto no encontrado con id: " + productId));
        
        // Verificar que el producto no esté ya en el carrusel
        if (carouselItemRepository.existsByProductId(productId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "El producto ya está en el carrusel");
        }
        
        // Verificar que el producto tenga stock
        if (product.getStock() == null || product.getStock() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "El producto debe tener stock disponible para estar en el carrusel");
        }
        
        // Verificar que el producto tenga imágenes
        if (product.getImages() == null || product.getImages().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "El producto debe tener al menos una imagen para estar en el carrusel");
        }
        
        // Verificar que no se exceda el límite de 5 productos
        long currentCount = carouselItemRepository.count();
        if (currentCount >= MAX_CAROUSEL_ITEMS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "El carrusel ya tiene el máximo de " + MAX_CAROUSEL_ITEMS + " productos. Elimina uno antes de agregar otro.");
        }
        
        // Crear el item del carrusel
        CarouselItem item = new CarouselItem();
        item.setProduct(product);
        item.setDisplayOrder((int) currentCount + 1);
        
        return carouselItemRepository.save(item);
    }
    
    @Override
    @Transactional
    public void removeProductFromCarousel(Long productId) {
        CarouselItem item = carouselItemRepository.findByProductId(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "El producto no está en el carrusel"));
        
        int removedOrder = item.getDisplayOrder();
        carouselItemRepository.delete(item);
            
        // Reordenar los items restantes
        List<CarouselItem> remainingItems = carouselItemRepository.findAllOrdered();
        for (CarouselItem remainingItem : remainingItems) {
            if (remainingItem.getDisplayOrder() > removedOrder) {
                remainingItem.setDisplayOrder(remainingItem.getDisplayOrder() - 1);
                carouselItemRepository.save(remainingItem);
            }
        }
    }
    
    @Override
    @Transactional
    public void reorderCarouselItems(List<Long> carouselItemIds) {
        if (carouselItemIds == null || carouselItemIds.isEmpty()) {
            return;
        }
        
        for (int i = 0; i < carouselItemIds.size(); i++) {
            final int order = i + 1;
            final Long itemId = carouselItemIds.get(i);
            CarouselItem item = carouselItemRepository.findById(itemId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                            "Item del carrusel no encontrado con id: " + itemId));
            item.setDisplayOrder(order);
            carouselItemRepository.save(item);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isProductInCarousel(Long productId) {
        return carouselItemRepository.existsByProductId(productId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getCarouselItemCount() {
        return carouselItemRepository.count();
    }
}

