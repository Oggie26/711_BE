package com.example._1.dto.response;

import com.example._1.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String orderCode;
    private String accountId;
    private String storeName;
    private OrderStatus status;
    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private BigDecimal totalPrice;
    private Integer loyaltyPointsEarned;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
}
