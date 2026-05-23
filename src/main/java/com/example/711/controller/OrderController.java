package com.example._1.controller;

import com.example._1.dto.request.OrderRequest;
import com.example._1.dto.response.OrderResponse;
import com.example._1.entity.OrderStatus;
import com.example._1.response.ApiResponse;
import com.example._1.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Controller")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order (User only)")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request, Authentication authentication) {
        return ApiResponse.<OrderResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Order created successfully")
                .data(orderService.createOrder(authentication.getName(), request))
                .build();
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Get current user's orders (User only)")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<List<OrderResponse>> getMyOrders(Authentication authentication) {
        return ApiResponse.<List<OrderResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Orders retrieved successfully")
                .data(orderService.getOrdersByUser(authentication.getName()))
                .build();
    }

    @GetMapping
    @Operation(summary = "Get all orders (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<OrderResponse>> getAllOrders() {
        return ApiResponse.<List<OrderResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("All orders retrieved successfully")
                .data(orderService.getAllOrders())
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ApiResponse<OrderResponse> getOrderDetails(@PathVariable Long id) {
        return ApiResponse.<OrderResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Order details retrieved successfully")
                .data(orderService.getOrderDetails(id))
                .build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        return ApiResponse.<OrderResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Order status updated successfully")
                .data(orderService.updateOrderStatus(id, status))
                .build();
    }
}
