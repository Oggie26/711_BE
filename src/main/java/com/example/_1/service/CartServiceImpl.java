package com.example._1.service;

import com.example._1.dto.response.CartItemResponse;
import com.example._1.dto.response.CartResponse;
import com.example._1.entity.Cart;
import com.example._1.entity.CartItem;
import com.example._1.entity.Product;
import com.example._1.entity.User;
import com.example._1.enums.ErrorCode;
import com.example._1.exception.AppException;
import com.example._1.repository.CartRepository;
import com.example._1.repository.ProductRepository;
import com.example._1.service.interfaces.CartService;
import com.example._1.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    @Override
    @Transactional
    public void addProductToCart(Long productId, Integer quantity) {
        User user = userService.getCurrentUser();

        Cart cart = cartRepository.findByUser(user).orElse(null);
        if (cart == null) {
            cart = Cart.builder()
                    .user(user)
                    .items(new LinkedHashSet<>()) // ĐÃ SỬA: Khởi tạo bằng LinkedHashSet thay vì ArrayList để khớp Required Type Set
                    .totalPrice(BigDecimal.ZERO)
                    .build();
            cart = cartRepository.save(cart);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        final Long targetProductId = productId;
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(targetProductId))
                .findFirst()
                .orElse(null);

        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build();
            cart.getItems().add(item);
        }

        updateCartTotal(cart);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void removeProductFromCart(List<Long> productIds) {
        Cart cart = getCartEntity();

        // Sử dụng removeIf trên thực thể Set hoạt động mượt mà như List
        cart.getItems().removeIf(item -> productIds.contains(item.getProduct().getId()));

        updateCartTotal(cart);
        cartRepository.save(cart);
    }

    @Override
    public CartResponse getCart() {
        Cart cart;
        try {
            cart = getCartEntity();
        } catch (AppException e) {
            User user = userService.getCurrentUser();
            return CartResponse.builder()
                    .cartId(null)
                    .totalPrice(BigDecimal.ZERO)
                    .fullName(user != null ? user.getFullName() : "Guest")
                    .items(new ArrayList<>()) // Trả về List trống cho FE render
                    .build();
        }

        String fullName = (cart.getUser() != null) ? cart.getUser().getFullName() : "Guest";

        // Chuyển đổi dữ liệu sạch sẽ sang List để DTO Response đóng gói gửi về cho React
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> {
                    BigDecimal quantityBD = BigDecimal.valueOf(item.getQuantity());
                    BigDecimal totalItemPrice = item.getPrice().multiply(quantityBD);

                    return CartItemResponse.builder()
                            .productId(item.getProduct().getId())
                            .productName(item.getProduct().getName())
                            .thumbnail(item.getProduct().getThumbnail())
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .totalItemPrice(totalItemPrice)
                            .build();
                })
                .toList();

        return CartResponse.builder()
                .cartId(cart.getId())
                .totalPrice(cart.getTotalPrice())
                .fullName(fullName)
                .items(itemResponses)
                .build();
    }

    @Override
    @Transactional
    public void updateProductQuantityInCart(Long productId, Integer quantity) {
        Cart cart = getCartEntity();
        final Long targetProductId = productId;

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(targetProductId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(quantity);
        }

        updateCartTotal(cart);
        cartRepository.save(cart);
    }

    private void updateCartTotal(Cart cart) {
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            cart.setTotalPrice(BigDecimal.ZERO);
            return;
        }
        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
    }

    private Cart getCartEntity() {
        return cartRepository.findByUser(userService.getCurrentUser())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
    }
}