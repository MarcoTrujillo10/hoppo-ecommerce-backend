package com.example.HPPO_Backend.entity.dto;

import lombok.Data;

@Data
public class CartRequest {
    private Integer quantity;
    private Long userId;
}
