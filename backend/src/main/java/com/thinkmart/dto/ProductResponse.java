package com.thinkmart.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id, String slug, String name, String description, BigDecimal price, BigDecimal originalPrice,
        BigDecimal rating, Integer stock, String imageUrl, boolean featured, CategoryResponse category) {
}