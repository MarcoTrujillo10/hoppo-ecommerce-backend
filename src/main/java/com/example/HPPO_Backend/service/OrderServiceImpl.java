package com.example.HPPO_Backend.service;

import com.example.HPPO_Backend.entity.*;
import com.example.HPPO_Backend.entity.dto.OrderRequest;
import com.example.HPPO_Backend.repository.CartRepository;
import com.example.HPPO_Backend.repository.OrderRepository;
import com.example.HPPO_Backend.repository.ProductRepository;
import com.example.HPPO_Backend.repository.UserRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            CartRepository cartRepository,
                            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Page<Order> getOrders(PageRequest pageable) {
        return orderRepository.findAllWithItems(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    @Transactional
    public Order createOrder(OrderRequest orderRequest, User user) {

        if (orderRequest.getAddress() == null || orderRequest.getAddress().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La dirección es obligatoria");
        }

        if (orderRequest.getShipping() == null || orderRequest.getShipping().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El método de envío es obligatorio");
        }


        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Carrito no encontrado para el usuario con ID: " + user.getId()));


        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede crear una orden con un carrito vacío");
        }


        Order newOrder = new Order();
        newOrder.setAddress(orderRequest.getAddress().trim());
        newOrder.setShipping(orderRequest.getShipping().trim());
        newOrder.setUser(user);
        newOrder.setStatus(OrderStatus.CREATED);
        newOrder.setOrderDate(LocalDateTime.now());

        double total = 0.0;
        for (CartProduct cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            double priceAtPurchase = product.getPrice();


            if (product.getDiscount() != null && product.getDiscount() > 0 && product.getDiscount() < 100) {
                double discountMultiplier = 1 - (product.getDiscount() / 100.0);
                priceAtPurchase = product.getPrice() * discountMultiplier;
            }
            total += priceAtPurchase * cartItem.getQuantity();


            OrderItem orderItem = new OrderItem(product, cartItem.getQuantity(), priceAtPurchase, newOrder);
            newOrder.getItems().add(orderItem);
        }

        newOrder.setTotal(total);


        Order savedOrder = orderRepository.save(newOrder);


        cart.getItems().clear();
        cart.setQuantity(0);
        cartRepository.save(cart);

        return savedOrder;
    }

    @Override
    @Transactional
    public Order cancelOrder(Long orderId, User user) {
        // Cargar la orden con sus items y productos usando JOIN FETCH
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));

        // Forzar la inicialización de la colección lazy si es necesario
        if (order.getItems() != null) {
            order.getItems().size(); // Esto fuerza la carga de la colección lazy
            // También forzar la carga de los productos dentro de cada item
            for (OrderItem item : order.getItems()) {
                if (item.getProduct() != null) {
                    item.getProduct().getName(); // Forzar la carga del producto
                }
            }
        }

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para cancelar esta orden");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La orden ya ha sido cancelada");
        }

        // Devolver el stock de todos los productos de la orden
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            System.out.println("🔄 Devolviendo stock para " + order.getItems().size() + " items de la orden #" + orderId);
            
            for (OrderItem item : order.getItems()) {
                if (item.getProduct() == null) {
                    System.err.println("⚠️ Error: OrderItem tiene producto null");
                    continue;
                }
                
                Product product = item.getProduct();
                Long productId = product.getId();
                Integer quantity = item.getQuantity();
                
                if (productId == null || quantity == null) {
                    System.err.println("⚠️ Error: Product ID o quantity es null");
                    continue;
                }
                
                System.out.println("📦 Procesando producto ID: " + productId + ", cantidad: " + quantity);
                
                // Cargar el producto actualizado desde la base de datos
                Product currentProduct = productRepository.findById(productId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                                "Producto no encontrado: " + productId));
                
                int oldStock = currentProduct.getStock();
                int newStock = oldStock + quantity;
                
                System.out.println("📊 Stock anterior: " + oldStock + ", cantidad a devolver: " + quantity + ", stock nuevo: " + newStock);
                
                // Devolver el stock
                currentProduct.setStock(newStock);
                Product savedProduct = productRepository.saveAndFlush(currentProduct);
                
                System.out.println("✅ Stock devuelto para producto ID: " + productId + ", stock actual: " + savedProduct.getStock());
            }
        } else {
            System.out.println("⚠️ La orden #" + orderId + " no tiene items o la lista está vacía. Items: " + (order.getItems() == null ? "null" : "empty"));
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.saveAndFlush(order);
        
        System.out.println("✅ Orden #" + orderId + " cancelada exitosamente");
        
        return savedOrder;
    }



    @Override
    public Page<Order> getMyOrders(User user, PageRequest pageRequest) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(user.getId(), pageRequest);
    }

    @Override
    @Transactional
    public Order updateOrder(Long orderId, OrderRequest orderRequest, User user) {
        // Cargar la orden con sus items y productos
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));


        if (!order.getUser().getId().equals(user.getId()) &&
                !user.getRole().equals(Role.VENDEDOR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para actualizar esta orden");
        }


        if (orderRequest.getAddress() != null && !orderRequest.getAddress().trim().isEmpty()) {
            order.setAddress(orderRequest.getAddress().trim());
        }


        if (orderRequest.getShipping() != null && !orderRequest.getShipping().trim().isEmpty()) {
            order.setShipping(orderRequest.getShipping().trim());
        }


        if (orderRequest.getStatus() != null) {
            OrderStatus oldStatus = order.getStatus();
            OrderStatus newStatus = orderRequest.getStatus();
            
            // Si se está cambiando a CANCELLED, devolver el stock
            if (oldStatus != OrderStatus.CANCELLED && newStatus == OrderStatus.CANCELLED) {
                // Devolver el stock de todos los productos de la orden
                if (order.getItems() != null && !order.getItems().isEmpty()) {
                    for (OrderItem item : order.getItems()) {
                        Product product = item.getProduct();
                        // Cargar el producto actualizado desde la base de datos
                        Product currentProduct = productRepository.findById(product.getId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                                        "Producto no encontrado: " + product.getId()));
                        
                        // Devolver el stock
                        currentProduct.setStock(currentProduct.getStock() + item.getQuantity());
                        productRepository.save(currentProduct);
                    }
                }
            }
            
            order.setStatus(newStatus);
        }

        return orderRepository.save(order);
    }
}
