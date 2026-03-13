package com.example.HPPO_Backend.entity.dto;

import com.example.HPPO_Backend.entity.Cart;
import com.example.HPPO_Backend.entity.CartProduct;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CartResponse {
    private Long id;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private List<CartProductResponse> items;
    private Double totalPrice;
    private Double originalTotalPrice;
    private Double totalDiscount;

    public static CartResponse fromCart(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setQuantity(cart.getQuantity());
        response.setCreatedAt(cart.getCreatedAt());
        response.setExpiresAt(cart.getExpiresAt());
        
        if (cart.getItems() != null) {
            response.setItems(cart.getItems().stream()
                    .map(CartProductResponse::fromCartProduct)
                    .collect(Collectors.toList()));
            
            // Calcular totales
            response.setTotalPrice(cart.getItems().stream()
                    .mapToDouble(CartProduct::getTotalPrice)
                    .sum());
            
            response.setOriginalTotalPrice(cart.getItems().stream()
                    .mapToDouble(CartProduct::getOriginalTotalPrice)
                    .sum());
            
            response.setTotalDiscount(response.getOriginalTotalPrice() - response.getTotalPrice());
        } else {
            response.setTotalPrice(0.0);
            response.setOriginalTotalPrice(0.0);
            response.setTotalDiscount(0.0);
        }
        
        return response;
    }
}
