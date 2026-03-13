package com.example.HPPO_Backend.entity.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class ProductRequest {
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String name;
    
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    private Double price;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 10000, message = "La descripción no puede exceder 10,000 caracteres")
    private String description;
    
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
    
    @NotNull(message = "La marca es obligatoria")
    private Long brandId;
    
    @Min(value = 0, message = "El descuento no puede ser negativo")
    @Max(value = 100, message = "El descuento no puede ser mayor a 100%")
    private Integer discount;
    
    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;
    
    private List<String> imageUrls;
}
