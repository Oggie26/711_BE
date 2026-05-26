package com.example._1.repository;

import com.example._1.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    void delete(CartItem cartItem);
}
