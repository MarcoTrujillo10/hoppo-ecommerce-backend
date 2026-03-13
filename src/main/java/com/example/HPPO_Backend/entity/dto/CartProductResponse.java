package com.example.HPPO_Backend.entity.dto;

import com.example.HPPO_Backend.entity.CartProduct;
import lombok.Data;

@Data
public class CartProductResponse {
    private Long id;
    private Integer quantity;
    private ProductResponse product;
    private Double unitPrice;
    private Double totalPrice;
    private Double originalTotalPrice;
    private Double totalDiscount;
    private boolean hasDiscount;

    public static CartProductResponse fromCartProduct(CartProduct cartProduct) {
        CartProductResponse response = new CartProductResponse();
        response.setId(cartProduct.getId());
        response.setQuantity(cartProduct.getQuantity());
        response.setProduct(ProductResponse.fromProduct(cartProduct.getProduct()));
        response.setUnitPrice(cartProduct.getUnitPrice());
        response.setTotalPrice(cartProduct.getTotalPrice());
        response.setOriginalTotalPrice(cartProduct.getOriginalTotalPrice());
        response.setTotalDiscount(cartProduct.getTotalDiscount());
        response.setHasDiscount(cartProduct.hasDiscount());
        return response;
    }
}
