package com.thinkmart.service;

import com.thinkmart.dto.CheckoutRequest;
import com.thinkmart.dto.OrderItemResponse;
import com.thinkmart.dto.OrderResponse;
import com.thinkmart.entity.Order;
import com.thinkmart.entity.OrderItem;
import com.thinkmart.entity.Product;
import com.thinkmart.exception.ApiException;
import com.thinkmart.repository.CartRepository;
import com.thinkmart.repository.OrderRepository;
import com.thinkmart.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    private final CartRepository carts;
    private final OrderRepository orders;
    private final ProductRepository products;

    public OrderService(CartRepository carts, OrderRepository orders, ProductRepository products) {
        this.carts = carts;
        this.orders = orders;
        this.products = products;
    }

    @Transactional
    public OrderResponse placeOrder(String email, CheckoutRequest request) {
        var cart = carts.findByUserEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Your cart is empty."));
        if (cart.getItems().isEmpty()) throw new ApiException(HttpStatus.BAD_REQUEST, "Your cart is empty.");
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setCustomerName(request.customerName().trim());
        order.setCustomerEmail(request.customerEmail().trim().toLowerCase());
        order.setCustomerPhone(request.customerPhone().trim());
        order.setDeliveryAddress(request.deliveryAddress().trim());
        BigDecimal total = BigDecimal.ZERO;
        for (var cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new ApiException(HttpStatus.CONFLICT, product.getName() + " no longer has enough stock.");
            }
            product.setStock(product.getStock() - cartItem.getQuantity());
            OrderItem item = new OrderItem();
            item.setProductName(product.getName());
            item.setImageUrl(product.getImageUrl());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(cartItem.getQuantity());
            order.addItem(item);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        order.setTotal(total);
        Order saved = orders.save(order);
        cart.clearItems();
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> myOrders(String email) {
        return orders.findByUserEmailOrderByCreatedAtDesc(email).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(String email, Long id) {
        return orders.findWithItemsByIdAndUserEmail(id, email).map(this::toResponse)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Order not found."));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream().map(item ->
                new OrderItemResponse(item.getProductName(), item.getImageUrl(), item.getUnitPrice(), item.getQuantity())).toList();
        return new OrderResponse(order.getId(), order.getStatus(), order.getTotal(), order.getCustomerName(),
                order.getCustomerEmail(), order.getCustomerPhone(), order.getDeliveryAddress(), order.getCreatedAt(), items);
    }
}