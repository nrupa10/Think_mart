package com.thinkmart.dto;

import com.thinkmart.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(Long id, OrderStatus status, BigDecimal total, String customerName,
                            String customerEmail, String customerPhone, String deliveryAddress,
                            Instant createdAt, List<OrderItemResponse> items) {
}