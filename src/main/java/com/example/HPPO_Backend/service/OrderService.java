package com.example.HPPO_Backend.service;

import com.example.HPPO_Backend.entity.Order;
import com.example.HPPO_Backend.entity.User;
import com.example.HPPO_Backend.entity.dto.OrderRequest;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface OrderService {
    Page<Order> getOrders(PageRequest pageRequest);
    Optional<Order> getOrderById(Long orderId);
    Order createOrder(OrderRequest orderRequest,User user);
    Order cancelOrder(Long orderId, User user);
    Order updateOrder(Long orderId, OrderRequest orderRequest, User user);
    Page<Order> getMyOrders(User user, PageRequest pageRequest);

}
