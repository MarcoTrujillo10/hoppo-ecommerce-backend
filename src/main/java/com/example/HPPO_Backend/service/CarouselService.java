package com.example.HPPO_Backend.service;

import com.example.HPPO_Backend.entity.CarouselItem;

import java.util.List;

public interface CarouselService {
    List<CarouselItem> getActiveCarouselItems();
    List<CarouselItem> getAllCarouselItems();
    CarouselItem addProductToCarousel(Long productId);
    void removeProductFromCarousel(Long productId);
    void reorderCarouselItems(List<Long> carouselItemIds);
    boolean isProductInCarousel(Long productId);
    long getCarouselItemCount();
}

