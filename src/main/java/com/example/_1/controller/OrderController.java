package com.example._1.controller;

import com.example._1.dto.response.*;
import com.example._1.enums.EnumPayment;
import com.example._1.service.interfaces.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Operation(summary = "Create a new order from a cart (User only)")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderResponse> createOrder(
            @Valid
            @RequestParam Long cartId,
            @RequestParam EnumPayment paymentMethod,
            HttpServletRequest request
    ) throws Exception {
        OrderResponse response = orderService.createOrder(cartId, paymentMethod, request);
        return ApiResponse.<OrderResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Order created successfully")
                .data(orderService.createOrder(cartId, paymentMethod, request))
                .redirectUrl(response.getPaymentUrl())
                .build();
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Get current user's latest order (User only)")
    public ApiResponse<List<OrderResponse>> getMyOrders() {
        return ApiResponse.<List<OrderResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Your latest order retrieved successfully")
                .data(orderService.getOrderBySelf())
                .build();
    }

    @GetMapping("/all")
    @Operation(summary = "Get all orders without pagination (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<OrderResponse>> getAllOrders() {
        return ApiResponse.<List<OrderResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("All orders retrieved successfully")
                .data(orderService.getAllOrders())
                .build();
    }

    @GetMapping
    @Operation(summary = "Search and paginate orders by orderCode (Admin / User)")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ApiResponse<PageResponse<OrderResponse>> searchOrders(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<OrderResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Orders queried successfully")
                .data(orderService.searchOrders(keyword, page, size))
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ApiResponse<OrderResponse> getOrderDetails(@PathVariable Long id) {
        return ApiResponse.<OrderResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Order details retrieved successfully")
                .data(orderService.getOrderById(id))
                .build();
    }

    @PatchMapping("/{id}/checkout")
    @Operation(summary = "Update order status to PAYMENT_SUCCESS")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ApiResponse<Void> checkOutOrder(@Valid @PathVariable Long id) {
        orderService.checkOut(id);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Order payment status updated to success successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel / Delete an order")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ApiResponse<OrderResponse> deleteOrder(@Valid @PathVariable Long id) {
        return ApiResponse.<OrderResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Order deleted successfully")
                .data(orderService.deleteOrder(id))
                .build();
    }




}