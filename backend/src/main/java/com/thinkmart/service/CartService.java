package com.thinkmart.service;

import com.thinkmart.dto.CartItemRequest;
import com.thinkmart.dto.CartItemResponse;
import com.thinkmart.dto.CartResponse;
import com.thinkmart.dto.UpdateCartItemRequest;
import com.thinkmart.entity.Cart;
import com.thinkmart.entity.CartItem;
import com.thinkmart.entity.Product;
import com.thinkmart.entity.User;
import com.thinkmart.exception.ApiException;
import com.thinkmart.repository.CartRepository;
import com.thinkmart.repository.ProductRepository;
import com.thinkmart.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CartService {
    private final CartRepository carts;
    private final ProductRepository products;
    private final UserRepository users;

    public CartService(CartRepository carts, ProductRepository products, UserRepository users) {
        this.carts = carts;
        this.products = products;
        this.users = users;
    }

    @Transactional
    public CartResponse getCart(String email) {
        return toResponse(loadCart(email));
    }

    @Transactional
    public CartResponse addItem(String email, CartItemRequest request) {
        Cart cart = loadCart(email);
        Product product = products.findWithCategoryById(request.productId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found."));
        CartItem item = cart.getItems().stream().filter(existing -> existing.getProduct().getId().equals(product.getId()))
                .findFirst().orElse(null);
        int quantity = request.quantity() + (item == null ? 0 : item.getQuantity());
        ensureStock(product, quantity);
        if (item == null) {
            item = new CartItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            cart.addItem(item);
        } else {
            item.setQuantity(quantity);
        }
        return toResponse(carts.save(cart));
    }

    @Transactional
    public CartResponse updateItem(String email, Long itemId, UpdateCartItemRequest request) {
        Cart cart = loadCart(email);
        CartItem item = cart.getItems().stream().filter(existing -> existing.getId().equals(itemId)).findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cart item not found."));
        ensureStock(item.getProduct(), request.quantity());
        item.setQuantity(request.quantity());
        return toResponse(carts.save(cart));
    }

    @Transactional
    public CartResponse removeItem(String email, Long itemId) {
        Cart cart = loadCart(email);
        CartItem item = cart.getItems().stream().filter(existing -> existing.getId().equals(itemId)).findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cart item not found."));
        cart.removeItem(item);
        return toResponse(carts.save(cart));
    }

    public Cart loadCart(String email) {
        return carts.findByUserEmail(email).orElseGet(() -> {
            User user = users.findByEmail(email)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found."));
            Cart cart = new Cart();
            cart.setUser(user);
            return carts.save(cart);
        });
    }

    public CartResponse toResponse(Cart cart) {
        var items = cart.getItems().stream().map(item -> {
            Product product = item.getProduct();
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            return new CartItemResponse(item.getId(), product.getId(), product.getName(), product.getImageUrl(),
                    product.getPrice(), product.getOriginalPrice(), item.getQuantity(), lineTotal);
        }).toList();
        BigDecimal subtotal = items.stream().map(CartItemResponse::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal savings = cart.getItems().stream().map(item -> item.getProduct().getOriginalPrice()
                        .subtract(item.getProduct().getPrice()).multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(items, subtotal.add(savings), savings, subtotal);
    }

    private void ensureStock(Product product, int quantity) {
        if (quantity > product.getStock()) {
            throw new ApiException(HttpStatus.CONFLICT, "Only " + product.getStock() + " units are currently available.");
        }
    }
}