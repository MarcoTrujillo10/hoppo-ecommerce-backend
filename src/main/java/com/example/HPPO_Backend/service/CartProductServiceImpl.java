package com.example.HPPO_Backend.service;

import com.example.HPPO_Backend.entity.Cart;
import com.example.HPPO_Backend.entity.CartProduct;
import com.example.HPPO_Backend.entity.Product;
import com.example.HPPO_Backend.entity.User;
import com.example.HPPO_Backend.entity.dto.CartProductRequest;
import com.example.HPPO_Backend.repository.CartProductRepository;
import com.example.HPPO_Backend.repository.CartRepository;
import com.example.HPPO_Backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CartProductServiceImpl implements CartProductService {
    @Autowired
    private CartProductRepository cartProductRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;

    public List<CartProduct> getCartProducts() {
        return cartProductRepository.findAll();
    }

    public List<CartProduct> getCartProductsByUser(Long userId) {
        return cartProductRepository.findByCartUserId(userId);
    }

    public Optional<CartProduct> getCartProductById(Long id) {
        return cartProductRepository.findById(id);
    }

    @Transactional
    public CartProduct createCartProduct(CartProductRequest request, User user) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        // Buscar carrito activo o crear uno nuevo
        Cart cart = cartRepository.findActiveCartByUserId(user.getId(), java.time.LocalDateTime.now())
                .orElseGet(() -> {
                    // Crear nuevo carrito si no existe uno activo
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setQuantity(0);
                    return cartRepository.save(newCart);
                });

        if (product.getStock() < request.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No hay suficiente stock para el producto: " + product.getName());
        }

        int newStock = product.getStock() - request.getQuantity();
        product.setStock(newStock);

        CartProduct cp = new CartProduct();
        cp.setQuantity(request.getQuantity());
        cp.setProduct(product);
        cp.setCart(cart);

        CartProduct savedCartProduct = cartProductRepository.save(cp);


        cart.getItems().add(savedCartProduct);

        cart.setQuantity(cart.getQuantity() + savedCartProduct.getQuantity());


        cartRepository.save(cart);

        return savedCartProduct;
    }

    @Override
    @Transactional
    public CartProduct updateCartProduct(Long id, CartProductRequest request) {
        CartProduct cartProduct = cartProductRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item del carrito no encontrado"));

        Product product = cartProduct.getProduct();
        int oldQuantity = cartProduct.getQuantity();
        int newQuantity = request.getQuantity();

        if (newQuantity <= 0) {
            deleteCartProduct(id);
            return null;
        }

        // Calcular la diferencia de stock
        int stockDifference = newQuantity - oldQuantity;
        
        if (product.getStock() < stockDifference) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No hay suficiente stock para actualizar el producto");
        }

        // Actualizar stock del producto
        product.setStock(product.getStock() - stockDifference);
        productRepository.save(product);

        // Actualizar cantidad del producto en el carrito
        cartProduct.setQuantity(newQuantity);
        CartProduct updatedCartProduct = cartProductRepository.save(cartProduct);

        // Actualizar cantidad total del carrito
        Cart cart = cartProduct.getCart();
        cart.setQuantity(cart.getQuantity() + stockDifference);
        cartRepository.save(cart);

        return updatedCartProduct;
    }

    @Override
    @Transactional
    public void deleteCartProduct(Long id) {
        CartProduct cartProduct = cartProductRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item del carrito no encontrado"));

        Product product = cartProduct.getProduct();
        int quantityToReturn = cartProduct.getQuantity();

        product.setStock(product.getStock() + quantityToReturn);
        productRepository.save(product);

        cartProductRepository.delete(cartProduct);
    }
}