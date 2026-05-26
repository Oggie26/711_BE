package com.example._1.dto.response;

import com.example._1.enums.EnumOrderStatus;
import com.example._1.enums.EnumPayment;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String orderCode;
    private String fullName;
    private UUID userId;
    private LocalDateTime orderDate;
    private EnumPayment payment;
    private EnumOrderStatus status;
    private BigDecimal totalPrice;
    private List<OrderItemResponse> items;
    private String paymentUrl;
}
