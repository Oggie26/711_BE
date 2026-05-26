package com.example._1.service.interfaces;

import com.example._1.dto.response.CartResponse;

import java.util.List;

public interface CartService {
    void addProductToCart(Long productId, Integer quantity);
    void removeProductFromCart(List<Long> productId);
    CartResponse getCart();
    void updateProductQuantityInCart(Long productId, Integer quantity);
}
