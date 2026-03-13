package com.example.HPPO_Backend.service;

import com.example.HPPO_Backend.entity.Brand;
import com.example.HPPO_Backend.entity.dto.BrandRequest;
import com.example.HPPO_Backend.exceptions.BrandDuplicateException;
import com.example.HPPO_Backend.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> getBrands() {
        return brandRepository.findAll();
    }

    public Optional<Brand> getBrandById(Long brandId) {
        return brandRepository.findById(brandId);
    }

    public Brand createBrand(BrandRequest brandRequest) throws BrandDuplicateException {
        List<Brand> brands = brandRepository.findByName(brandRequest.getName());
        if (brands.isEmpty()) {
            Brand brand = new Brand();
            brand.setName(brandRequest.getName());
            return brandRepository.save(brand);
        }
        throw new BrandDuplicateException();
    }
    @Override
    public void deleteBrand(Long brandId) {
        if (!brandRepository.existsById(brandId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Marca no encontrada con id: " + brandId);
        }
        brandRepository.deleteById(brandId);
    }
}
