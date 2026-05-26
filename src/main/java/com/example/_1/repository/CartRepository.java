package com.example._1.repository;

import com.example._1.entity.Cart;
import com.example._1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(String userId);
    Optional<Cart> findByUser(User user);
}
