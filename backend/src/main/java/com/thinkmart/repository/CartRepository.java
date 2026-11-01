package com.thinkmart.repository;

import com.thinkmart.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @EntityGraph(attributePaths = {"items", "items.product", "items.product.category"})
    Optional<Cart> findByUserEmail(String email);
}