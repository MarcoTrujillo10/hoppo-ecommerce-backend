package com.example.HPPO_Backend.entity.dto;

public class OrderItemResponse {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private Integer discount;

    public OrderItemResponse() {}

    public OrderItemResponse(Long productId, String productName,
                             Integer quantity, Double price, Integer discount) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getDiscount() { return discount; }
    public void setDiscount(Integer discount) { this.discount = discount; }
}
