package com.thinkmart.controller;

import com.thinkmart.dto.CartItemRequest;
import com.thinkmart.dto.CartResponse;
import com.thinkmart.dto.UpdateCartItemRequest;
import com.thinkmart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse getCart(Principal principal) {
        return cartService.getCart(principal.getName());
    }

    @PostMapping("/items")
    public CartResponse addItem(Principal principal, @Valid @RequestBody CartItemRequest request) {
        return cartService.addItem(principal.getName(), request);
    }

    @PutMapping("/items/{itemId}")
    public CartResponse updateItem(Principal principal, @PathVariable Long itemId,
                                   @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(principal.getName(), itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    public CartResponse removeItem(Principal principal, @PathVariable Long itemId) {
        return cartService.removeItem(principal.getName(), itemId);
    }
}