package com.thinkmart.dto;

import java.math.BigDecimal;

public record OrderItemResponse(String productName, String imageUrl, BigDecimal unitPrice, int quantity) {
}