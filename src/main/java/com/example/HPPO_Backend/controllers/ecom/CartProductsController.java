package com.example.HPPO_Backend.controllers.ecom;

import com.example.HPPO_Backend.entity.CartProduct;
import com.example.HPPO_Backend.entity.User;
import com.example.HPPO_Backend.entity.dto.CartProductRequest;
import com.example.HPPO_Backend.entity.dto.CartProductResponse;
import com.example.HPPO_Backend.service.CartProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // ¡Importar!
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cart-products")
public class CartProductsController {
    @Autowired
    private CartProductService cartProductService;

    @GetMapping
    public ResponseEntity<List<CartProductResponse>> getCartProducts(@AuthenticationPrincipal User user) {
        List<CartProduct> cartProducts = this.cartProductService.getCartProductsByUser(user.getId());
        List<CartProductResponse> responses = cartProducts.stream()
                .map(CartProductResponse::fromCartProduct)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<CartProductResponse> getCartProductById(@PathVariable Long id) {
        Optional<CartProduct> result = this.cartProductService.getCartProductById(id);
        return result.map(cartProduct -> ResponseEntity.ok(CartProductResponse.fromCartProduct(cartProduct)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping
    public ResponseEntity<Object> createCartProduct(@RequestBody CartProductRequest request, @AuthenticationPrincipal User user) {
        CartProduct result = this.cartProductService.createCartProduct(request, user);
        return ResponseEntity.created(URI.create("/cart-products/" + result.getId())).body(CartProductResponse.fromCartProduct(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartProductResponse> updateCartProduct(@PathVariable Long id, @RequestBody CartProductRequest request) {
        CartProduct result = this.cartProductService.updateCartProduct(id, request);
        return ResponseEntity.ok(CartProductResponse.fromCartProduct(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCartProduct(@PathVariable Long id) {
        this.cartProductService.deleteCartProduct(id);
        return ResponseEntity.noContent().build();
    }
}