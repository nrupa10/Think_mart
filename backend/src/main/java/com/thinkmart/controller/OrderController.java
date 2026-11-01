package com.thinkmart.controller;

import com.thinkmart.dto.CheckoutRequest;
import com.thinkmart.dto.OrderResponse;
import com.thinkmart.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(Principal principal, @Valid @RequestBody CheckoutRequest request) {
        return orderService.placeOrder(principal.getName(), request);
    }

    @GetMapping("/my-orders")
    public List<OrderResponse> myOrders(Principal principal) {
        return orderService.myOrders(principal.getName());
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(Principal principal, @PathVariable Long id) {
        return orderService.getOrder(principal.getName(), id);
    }
}