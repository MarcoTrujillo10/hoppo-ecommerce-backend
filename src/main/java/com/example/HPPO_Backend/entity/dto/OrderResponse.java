package com.example.HPPO_Backend.entity.dto;

import com.example.HPPO_Backend.entity.Order;
import com.example.HPPO_Backend.entity.OrderItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderResponse {
    private Long id;
    private String address;
    private String shipping;
    private Double total;
    private LocalDateTime orderDate;
    private String status;
    private List<OrderItemResponse> items;
    private UserInfo user;

    public static class UserInfo {
        private Long id;
        private String name;
        private String lastName;
        private String email;

        public UserInfo() {}

        public UserInfo(Long id, String name, String lastName, String email) {
            this.id = id;
            this.name = name;
            this.lastName = lastName;
            this.email = email;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public OrderResponse() {}

    public static OrderResponse fromOrder(Order order) {
        OrderResponse dto = new OrderResponse();
        dto.id = order.getId();
        dto.address = order.getAddress();
        dto.shipping = order.getShipping();
        dto.total = order.getTotal();
        dto.orderDate = order.getOrderDate();
        dto.status = order.getStatus() != null ? order.getStatus().name() : null;

        if (order.getUser() != null) {
            dto.user = new UserInfo(
                order.getUser().getId(),
                order.getUser().getName(),
                order.getUser().getLastName(),
                order.getUser().getEmail()
            );
        }

        dto.items = order.getItems().stream()
                .map(OrderResponse::mapItem)
                .collect(Collectors.toList());

        return dto;
    }

    private static OrderItemResponse mapItem(OrderItem item) {
        return new OrderItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPrice(),
                item.getProduct().getDiscount()
        );
    }

    public Long getId() { return id; }
    public String getAddress() { return address; }
    public String getShipping() { return shipping; }
    public Double getTotal() { return total; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public List<OrderItemResponse> getItems() { return items; }
    public UserInfo getUser() { return user; }

    public void setId(Long id) { this.id = id; }
    public void setAddress(String address) { this.address = address; }
    public void setShipping(String shipping) { this.shipping = shipping; }
    public void setTotal(Double total) { this.total = total; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public void setStatus(String status) { this.status = status; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }
    public void setUser(UserInfo user) { this.user = user; }
}
