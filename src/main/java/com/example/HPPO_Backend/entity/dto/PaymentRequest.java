package com.example.HPPO_Backend.entity.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PaymentRequest {
    @NotBlank(message = "El número de tarjeta es obligatorio")
    @Pattern(regexp = "^[0-9]{16}$", message = "El número de tarjeta debe tener 16 dígitos")
    private String cardNumber;

    @NotBlank(message = "El nombre del titular es obligatorio")
    @Size(max = 100, message = "El nombre del titular no puede exceder 100 caracteres")
    private String cardHolderName;

    @NotBlank(message = "La fecha de expiración es obligatoria")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "La fecha debe estar en formato MM/YY")
    private String expiryDate;

    @NotBlank(message = "El CVV es obligatorio")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "El CVV debe tener 3 o 4 dígitos")
    private String cvv;

    @NotBlank(message = "El método de pago es obligatorio")
    private String paymentMethod; // "credit_card", "debit_card"

    @NotNull(message = "El monto total es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private Double totalAmount;

    @NotBlank(message = "La dirección de facturación es obligatoria")
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String billingAddress;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String city;

    @NotBlank(message = "El código postal es obligatorio")
    @Size(max = 20, message = "El código postal no puede exceder 20 caracteres")
    private String postalCode;

    @NotBlank(message = "El país es obligatorio")
    @Size(max = 100, message = "El país no puede exceder 100 caracteres")
    private String country;
}
