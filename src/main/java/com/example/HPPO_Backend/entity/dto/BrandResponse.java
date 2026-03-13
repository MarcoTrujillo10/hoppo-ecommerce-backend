package com.example.HPPO_Backend.entity.dto;

import com.example.HPPO_Backend.entity.Brand;
import lombok.Data;

@Data
public class BrandResponse {
    private Long id;
    private String name;

    public static BrandResponse fromBrand(Brand brand) {
        BrandResponse response = new BrandResponse();
        response.setId(brand.getId());
        response.setName(brand.getName());
        return response;
    }
}
