package com.example.HPPO_Backend.controllers.ecom;

import com.example.HPPO_Backend.entity.CarouselItem;
import com.example.HPPO_Backend.entity.dto.CarouselItemResponse;
import com.example.HPPO_Backend.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/carousel")
public class EcomCarouselController {
    
    @Autowired
    private CarouselService carouselService;
    
    @GetMapping
    public ResponseEntity<List<CarouselItemResponse>> getActiveCarouselItems() {
        List<CarouselItem> items = carouselService.getActiveCarouselItems();
        List<CarouselItemResponse> responses = items.stream()
                .map(CarouselItemResponse::fromCarouselItem)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}

