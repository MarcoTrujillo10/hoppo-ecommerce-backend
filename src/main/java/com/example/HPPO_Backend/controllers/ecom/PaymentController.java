package com.example.HPPO_Backend.controllers.ecom;

import com.example.HPPO_Backend.entity.User;
import com.example.HPPO_Backend.entity.dto.PaymentRequest;
import com.example.HPPO_Backend.entity.dto.PaymentResponse;
import com.example.HPPO_Backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody PaymentRequest paymentRequest,
            @AuthenticationPrincipal User user) {
        
        PaymentResponse response = paymentService.processFakePayment(paymentRequest, user);
        return ResponseEntity.ok(response);
    }
}
