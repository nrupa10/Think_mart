package com.thinkmart.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CheckoutRequest(
        @NotBlank @Size(max = 100) String customerName,
        @NotBlank @Email @Size(max = 190) String customerEmail,
        @NotBlank @Pattern(regexp = "^[+0-9() .-]{7,30}$") String customerPhone,
        @NotBlank @Size(max = 1500) String deliveryAddress) {
}