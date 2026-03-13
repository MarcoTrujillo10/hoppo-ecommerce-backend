package com.example.HPPO_Backend.service;

import com.example.HPPO_Backend.entity.CartProduct;
import com.example.HPPO_Backend.entity.User; // Importar
import com.example.HPPO_Backend.entity.dto.CartProductRequest;

import java.util.List;
import java.util.Optional;

public interface CartProductService {
    List<CartProduct> getCartProducts();
    List<CartProduct> getCartProductsByUser(Long userId);
    Optional<CartProduct> getCartProductById(Long id);
    CartProduct createCartProduct(CartProductRequest request, User user); // Pasar el usuario
    CartProduct updateCartProduct(Long id, CartProductRequest request);
    void deleteCartProduct(Long id);
}