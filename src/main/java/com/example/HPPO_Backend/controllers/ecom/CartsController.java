package com.example.HPPO_Backend.controllers.ecom;
 
import com.example.HPPO_Backend.entity.Cart;
import com.example.HPPO_Backend.entity.User;
import com.example.HPPO_Backend.entity.dto.CartRequest;
import com.example.HPPO_Backend.entity.dto.CartResponse;
import com.example.HPPO_Backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.net.URI;
import java.util.Optional;
 
@RestController
@RequestMapping("carts")
public class CartsController {
    @Autowired
    private CartService cartService ;
 
    @GetMapping
    public ResponseEntity<Page<Cart>> getCarts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page == null || size == null)
            return ResponseEntity.ok(cartService.getCarts(PageRequest.of(0, Integer.MAX_VALUE)));
        return ResponseEntity.ok(cartService.getCarts(PageRequest.of(page, size)));
    }
 

 
    @PostMapping
    public ResponseEntity<Object> createCart(@RequestBody CartRequest cartRequest) {
        Cart result = this.cartService.createCart(cartRequest);
        return ResponseEntity.created(URI.create("/carts/" + result.getId())).body(result);
    }

    @GetMapping("/my-cart")
    public ResponseEntity<CartResponse> getMyCart(@AuthenticationPrincipal User user) {
        // Buscar carrito activo (no expirado) del usuario
        Optional<Cart> result = this.cartService.getActiveCartByUserId(user.getId());
        return result.map(cart -> ResponseEntity.ok(CartResponse.fromCart(cart)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping({"/{cartId}"})
    public ResponseEntity<CartResponse> getCartById(@PathVariable Long cartId) {
        Optional<Cart> result = this.cartService.getCartById(cartId);
        return result.map(cart -> ResponseEntity.ok(CartResponse.fromCart(cart)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /**
     * Extiende la expiración del carrito del usuario autenticado
     */
    @PostMapping("/my-cart/extend")
    public ResponseEntity<String> extendMyCartExpiration(@AuthenticationPrincipal User user) {
        Optional<Cart> cart = this.cartService.getCartByUserId(user.getId());
        if (cart.isPresent()) {
            this.cartService.extendCartExpiration(cart.get().getId());
            return ResponseEntity.ok("Expiración del carrito extendida por 24 horas");
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Extiende la expiración de un carrito específico
     */
    @PostMapping("/{cartId}/extend")
    public ResponseEntity<String> extendCartExpiration(@PathVariable Long cartId) {
        try {
            this.cartService.extendCartExpiration(cartId);
            return ResponseEntity.ok("Expiración del carrito extendida por 24 horas");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint administrativo para limpiar carritos expirados manualmente
     */
    @PostMapping("/admin/cleanup")
    public ResponseEntity<String> cleanupExpiredCarts() {
        try {
            int deletedCount = this.cartService.deleteExpiredCarts();
            return ResponseEntity.ok("Carritos expirados eliminados: " + deletedCount);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error durante la limpieza: " + e.getMessage());
        }
    }

}
