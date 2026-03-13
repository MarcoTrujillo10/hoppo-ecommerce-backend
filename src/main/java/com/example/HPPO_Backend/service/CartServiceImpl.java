package com.example.HPPO_Backend.service;

import com.example.HPPO_Backend.entity.Cart;
import com.example.HPPO_Backend.entity.User;
import com.example.HPPO_Backend.entity.dto.CartRequest;
import com.example.HPPO_Backend.repository.CartRepository;
import com.example.HPPO_Backend.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<Cart> getCarts(PageRequest pageable) {
        Page<Cart> page = cartRepository.findAll(pageable);
        LocalDateTime now = LocalDateTime.now();

        
        List<Cart> processed = page.getContent().stream().map(cart -> {
            if (cart.getExpiresAt() != null && cart.getExpiresAt().isBefore(now)) {
                if (cart.getItems() != null) {
                    cart.getItems().clear();
                }
                cart.setQuantity(0);
                cart.extendExpiration();
                return cartRepository.save(cart);
            }
            return cart;
        }).collect(Collectors.toList());

        List<Cart> valid = processed.stream()
                .filter(cart -> cart.getExpiresAt() != null && cart.getExpiresAt().isAfter(now))
                .collect(Collectors.toList());

        return new PageImpl<>(valid, pageable, valid.size());
    }

    @Override
    public Optional<Cart> getCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .map(cart -> {
                    if (cart.getExpiresAt() != null && cart.getExpiresAt().isBefore(LocalDateTime.now())) {
                        if (cart.getItems() != null) {
                            cart.getItems().clear();
                        }
                        cart.setQuantity(0);
                        cart.extendExpiration();
                        return cartRepository.save(cart);
                    }
                    return cart;
                });
    }

    @Override
    public Cart createCart(CartRequest cartRequest) {
        if (cartRequest.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId es obligatorio");
        }

        // Buscar carrito activo (no expirado)
        Optional<Cart> existingActive = cartRepository.findActiveCartByUserId(cartRequest.getUserId(), LocalDateTime.now());
        if (existingActive.isPresent()) {
            return existingActive.get();
        }

        Optional<Cart> existingExpired = cartRepository.findByUserId(cartRequest.getUserId());
        if (existingExpired.isPresent() && existingExpired.get().isExpired()) {
            Cart expired = existingExpired.get();
            if (expired.getItems() != null) {
                expired.getItems().clear();
            }
            expired.setQuantity(0);
            expired.extendExpiration();
            return cartRepository.save(expired);
        }

        // Crear nuevo carrito
        User user = userRepository.findById(cartRequest.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: id=" + cartRequest.getUserId()));

        Cart cart = new Cart();
        cart.setQuantity(cartRequest.getQuantity() == null ? 0 : cartRequest.getQuantity());
        cart.setUser(user);
        cart.extendExpiration(); // opcional, si querés que siempre arranque con fecha de expiración nueva

        return cartRepository.save(cart);
    }

    @Override
    public Optional<Cart> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(cart -> {
                    if (cart.getExpiresAt() != null && cart.getExpiresAt().isBefore(LocalDateTime.now())) {
                        if (cart.getItems() != null) {
                            cart.getItems().clear();
                        }
                        cart.setQuantity(0);
                        cart.extendExpiration();
                        return cartRepository.save(cart);
                    }
                    return cart;
                });
    }

    @Override
    public Optional<Cart> getActiveCartByUserId(Long userId) {
        return cartRepository.findActiveCartByUserId(userId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void extendCartExpiration(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrito no encontrado: id=" + cartId));

        cart.extendExpiration();
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void cleanExpiredCarts() {
        List<Cart> expiredCarts = cartRepository.findExpiredCarts(LocalDateTime.now());
        for (Cart cart : expiredCarts) {
            if (cart.getItems() != null) {
                cart.getItems().clear();
            }
            cart.setQuantity(0);
            cart.extendExpiration();
            cartRepository.save(cart);
        }
    }

    @Override
    @Transactional
    public int deleteExpiredCarts() {
        List<Cart> expiredCarts = cartRepository.findExpiredCarts(LocalDateTime.now());
        for (Cart cart : expiredCarts) {
            if (cart.getItems() != null) {
                cart.getItems().clear();
            }
            cart.setQuantity(0);
            cart.extendExpiration();
            cartRepository.save(cart);
        }
        return expiredCarts.size();
    }
}
