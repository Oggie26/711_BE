package com.example._1.controller;

import com.example._1.dto.request.CartItemRequest;
import com.example._1.dto.response.ApiResponse;
import com.example._1.dto.response.CartResponse;
import com.example._1.service.interfaces.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
@Tag(name = "Cart Controller")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Xem giỏ hàng")
    public ApiResponse<CartResponse> getCart() {
        return ApiResponse.<CartResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Cart retrieved successfully")
                .data(cartService.getCart())
                .build();
    }

    @PostMapping("/items")
    @Operation(summary = "Thêm sản phẩm vào giỏ hàng")
    public ApiResponse<Void> addProduct(@Valid @RequestBody CartItemRequest request) {
        cartService.addProductToCart(request.getProductId(), request.getQuantity());
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Product added to cart successfully")
                .build();
    }

    @PatchMapping("/items")
    @Operation(summary = "Cập nhật số lượng sản phẩm")
    public ApiResponse<Void> updateQuantity(@Valid @RequestBody CartItemRequest request) {
        cartService.updateProductQuantityInCart(request.getProductId(), request.getQuantity());
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Cart item updated successfully")
                .build();
    }

    @DeleteMapping("/items")
    @Operation(summary = "Xóa sản phẩm khỏi giỏ hàng")
    public ApiResponse<Void> removeItems(@RequestBody List<Long> productId) {
        cartService.removeProductFromCart(productId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Products removed from cart successfully")
                .build();
    }
}