package com.thinkmart.repository;

import com.thinkmart.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "items")
    List<Order> findByUserEmailOrderByCreatedAtDesc(String email);

    @EntityGraph(attributePaths = "items")
    Optional<Order> findWithItemsByIdAndUserEmail(Long id, String email);
}