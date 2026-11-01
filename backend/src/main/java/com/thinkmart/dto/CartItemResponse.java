package com.thinkmart.dto;

import java.math.BigDecimal;

public record CartItemResponse(Long id, Long productId, String name, String imageUrl,
                               BigDecimal price, BigDecimal originalPrice, int quantity, BigDecimal lineTotal) {
}