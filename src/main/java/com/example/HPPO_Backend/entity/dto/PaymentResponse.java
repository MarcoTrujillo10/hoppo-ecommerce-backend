package com.example.HPPO_Backend.entity.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class PaymentResponse {
    private boolean success;
    private String transactionId;
    private String message;
    private String status;
    private Double amount;
    private String currency;
    private String orderId;
    private String paymentMethod;
    private String timestamp;
}
