package com.example.HPPO_Backend.service;

import com.example.HPPO_Backend.entity.User;
import com.example.HPPO_Backend.entity.dto.PaymentRequest;
import com.example.HPPO_Backend.entity.dto.PaymentResponse;
import com.example.HPPO_Backend.entity.Cart;
import com.example.HPPO_Backend.entity.Order;
import com.example.HPPO_Backend.entity.OrderItem;
import com.example.HPPO_Backend.entity.OrderStatus;
import com.example.HPPO_Backend.repository.CartRepository;
import com.example.HPPO_Backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Random;

@Service
public class PaymentService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    private final Random random = new Random();

    @Transactional
    public PaymentResponse processFakePayment(PaymentRequest paymentRequest, User user) {
        try {
            // Buscar el carrito activo del usuario
            Cart cart = cartRepository.findActiveCartByUserId(user.getId(), LocalDateTime.now())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró carrito activo"));

            // Validar que el carrito no esté vacío
            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito está vacío");
            }

            // Simular validación de tarjeta (siempre exitosa para este ejemplo)
            boolean isCardValid = validateCard(paymentRequest);
            
            if (!isCardValid) {
                return PaymentResponse.builder()
                        .success(false)
                        .message("Tarjeta de crédito inválida")
                        .status("DECLINED")
                        .build();
            }

            // Simular procesamiento de pago (95% de éxito)
            boolean paymentSuccessful = random.nextDouble() < 0.95;

            if (paymentSuccessful) {
                // Generar ID de transacción falso
                String transactionId = "TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                
                // Crear orden
                Order order = createOrderFromCart(cart, user, paymentRequest, transactionId);
                Order savedOrder = orderRepository.save(order);

                // Limpiar carrito después del pago exitoso
                cart.getItems().clear();
                cart.setQuantity(0);
                cartRepository.save(cart);

                return PaymentResponse.builder()
                        .success(true)
                        .transactionId(transactionId)
                        .message("Pago procesado exitosamente")
                        .status("COMPLETED")
                        .amount(paymentRequest.getTotalAmount())
                        .currency("USD")
                        .orderId(savedOrder.getId().toString())
                        .paymentMethod(paymentRequest.getPaymentMethod())
                        .timestamp(LocalDateTime.now().toString())
                        .build();
            } else {
                // Simular fallo de pago
                return PaymentResponse.builder()
                        .success(false)
                        .message("Pago rechazado por el banco")
                        .status("DECLINED")
                        .build();
            }

        } catch (Exception e) {
            return PaymentResponse.builder()
                    .success(false)
                    .message("Error procesando el pago: " + e.getMessage())
                    .status("ERROR")
                    .build();
        }
    }

    private boolean validateCard(PaymentRequest paymentRequest) {
        // Validación básica de tarjeta (siempre exitosa para este ejemplo)
        // En un sistema real, aquí se validaría con el procesador de pagos
        
        // Validar que el número de tarjeta no sea todos ceros
        return !paymentRequest.getCardNumber().matches("^0+$");
    }

    private Order createOrderFromCart(Cart cart, User user, PaymentRequest paymentRequest, String transactionId) {
        Order order = new Order();
        order.setUser(user);
        order.setTotal(paymentRequest.getTotalAmount());
        order.setStatus(OrderStatus.CREATED);
        order.setAddress(paymentRequest.getBillingAddress() + ", " + paymentRequest.getCity() + 
                        ", " + paymentRequest.getPostalCode() + ", " + paymentRequest.getCountry());
        order.setShipping("Standard Shipping");
        
        // Crear OrderItems a partir de CartProducts
        for (var cartProduct : cart.getItems()) {
            OrderItem orderItem = new OrderItem(
                cartProduct.getProduct(),
                cartProduct.getQuantity(),
                cartProduct.getProduct().getDiscountedPrice(),
                order
            );
            order.getItems().add(orderItem);
        }
        
        return order;
    }
}
