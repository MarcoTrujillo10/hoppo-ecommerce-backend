package com.example.HPPO_Backend.controllers.ecom;

import com.example.HPPO_Backend.entity.Cart;
import com.example.HPPO_Backend.entity.Order;
import com.example.HPPO_Backend.entity.User;
import com.example.HPPO_Backend.entity.dto.CartRequest;
import com.example.HPPO_Backend.entity.dto.OrderRequest;
import com.example.HPPO_Backend.entity.dto.OrderResponse;
import com.example.HPPO_Backend.service.OrderService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("orders")
public class OrdersController {
	
    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size){
    	
    	PageRequest pageRequest = (page != null && size != null)
                ? PageRequest.of(page, size)
                : PageRequest.of(0, Integer.MAX_VALUE);
       
        Page<Order> orders = orderService.getOrders(pageRequest);
        Page<OrderResponse> dtoPage = orders.map(OrderResponse::fromOrder);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping({"/{orderId}"})
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        Optional<Order> result = this.orderService.getOrderById(orderId);
        return result.map(order -> ResponseEntity.ok(OrderResponse.fromOrder(order))).orElseGet(() -> ResponseEntity.noContent().build());
    }
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long orderId,
            @RequestBody OrderRequest orderRequest,
            @AuthenticationPrincipal User user) {
        Order updatedOrder = this.orderService.updateOrder(orderId, orderRequest, user);
        return ResponseEntity.ok(OrderResponse.fromOrder(updatedOrder));
    }


    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderRequest orderRequest,
            @AuthenticationPrincipal User user) {
        Order result = this.orderService.createOrder(orderRequest, user);
        return ResponseEntity
        		.created(URI.create("/orders/" + result.getId()))
        		.body(OrderResponse.fromOrder(result));
    }
    
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId, @AuthenticationPrincipal User user) {
        Order cancelledOrder = this.orderService.cancelOrder(orderId, user);
        return ResponseEntity.ok(OrderResponse.fromOrder(cancelledOrder));

    }

    @GetMapping("/my-orders")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @AuthenticationPrincipal User user) {

        PageRequest pageRequest = (page != null && size != null)
                ? PageRequest.of(page, size)
                : PageRequest.of(0, Integer.MAX_VALUE);

        Page<Order> myOrders = orderService.getMyOrders(user, pageRequest);
        Page<OrderResponse> dtoPage = myOrders.map(OrderResponse::fromOrder);
        return ResponseEntity.ok(dtoPage);
    }

}