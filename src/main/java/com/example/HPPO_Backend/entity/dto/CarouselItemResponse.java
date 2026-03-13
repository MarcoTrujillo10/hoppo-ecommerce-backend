package com.example.HPPO_Backend.entity.dto;

import com.example.HPPO_Backend.entity.CarouselItem;
import lombok.Data;

@Data
public class CarouselItemResponse {
    private Long id;
    private ProductResponse product;
    private Integer displayOrder;
    
    public static CarouselItemResponse fromCarouselItem(CarouselItem item) {
        if (item == null) {
            return null;
        }
        CarouselItemResponse response = new CarouselItemResponse();
        response.setId(item.getId());
        if (item.getProduct() != null) {
            response.setProduct(ProductResponse.fromProduct(item.getProduct()));
        }
        response.setDisplayOrder(item.getDisplayOrder());
        return response;
    }
}


