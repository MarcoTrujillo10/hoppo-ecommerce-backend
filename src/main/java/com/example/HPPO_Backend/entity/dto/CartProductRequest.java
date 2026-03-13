package com.example.HPPO_Backend.entity.dto;

import lombok.Data;

@Data
public class CartProductRequest {
    private Integer quantity;
    private Long productId;

}
