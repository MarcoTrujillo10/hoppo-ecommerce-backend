package com.example.HPPO_Backend.service;

import com.example.HPPO_Backend.entity.Brand;
import com.example.HPPO_Backend.entity.dto.BrandRequest;
import com.example.HPPO_Backend.exceptions.BrandDuplicateException;

import java.util.List;
import java.util.Optional;

public interface BrandService {
    List<Brand> getBrands();
    Optional<Brand> getBrandById(Long brandId);
    Brand createBrand(BrandRequest brandRequest) throws BrandDuplicateException;
    void deleteBrand(Long brandId);
}
