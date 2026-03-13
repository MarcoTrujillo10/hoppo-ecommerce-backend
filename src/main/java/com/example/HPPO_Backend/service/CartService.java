package com.example.HPPO_Backend.service;
 
import com.example.HPPO_Backend.entity.Cart;
import com.example.HPPO_Backend.entity.dto.CartRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.Optional;
 
public interface CartService {
    Page<Cart> getCarts(PageRequest pageRequest);
    Optional<Cart> getCartById(Long cartId);
    Optional<Cart> getCartByUserId(Long userId);
    Cart createCart(CartRequest cartRequest);
    
 
    Optional<Cart> getActiveCartByUserId(Long userId);
    void extendCartExpiration(Long cartId);
    void cleanExpiredCarts();
    int deleteExpiredCarts();
}