package com.example.HPPO_Backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class CartProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer quantity;


    @ManyToOne
    @JoinColumn(name = "id_cart")
    @JsonIgnore
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;

    // Métodos para cálculos de precio con descuento
    public Double getUnitPrice() {
        return product != null ? product.getDiscountedPrice() : 0.0;
    }

    public Double getTotalPrice() {
        return quantity != null && product != null ? quantity * product.getDiscountedPrice() : 0.0;
    }

    public Double getOriginalTotalPrice() {
        return quantity != null && product != null ? quantity * product.getPrice() : 0.0;
    }

    public Double getTotalDiscount() {
        return getOriginalTotalPrice() - getTotalPrice();
    }

    
    public boolean hasDiscount() {
        return product != null && product.hasDiscount();
    }

}
