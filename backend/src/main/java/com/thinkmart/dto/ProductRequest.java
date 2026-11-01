package com.thinkmart.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(max = 140) String slug,
        @NotBlank @Size(max = 140) String name,
        @NotBlank @Size(max = 3000) String description,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @NotNull @DecimalMin("0.01") BigDecimal originalPrice,
        @NotNull @DecimalMin("0") @DecimalMax("5") BigDecimal rating,
        @NotNull @DecimalMin("0") Integer stock,
        @NotBlank @Size(max = 1000) String imageUrl,
        boolean featured,
        @NotNull Long categoryId) {
}