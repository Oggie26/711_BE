package com.example._1.service.interfaces;

import com.example._1.dto.request.OrderRequest;
import com.example._1.dto.response.OrderResponse;
import com.example._1.dto.response.PageResponse;
import com.example._1.dto.response.ProductResponse;
import com.example._1.enums.EnumPayment;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface OrderService {
    OrderResponse getOrderById(Long id);
    OrderResponse createOrder(Long cartId, EnumPayment paymentMethod, HttpServletRequest request) throws Exception;
    OrderResponse updateOrder(OrderRequest orderRequest);
    OrderResponse deleteOrder(Long id);
    List<OrderResponse> getAllOrders();
    PageResponse<OrderResponse> searchOrders(String request, int page, int size);
    List<OrderResponse> getOrderBySelf();
    void checkOut(Long orderId);
}
