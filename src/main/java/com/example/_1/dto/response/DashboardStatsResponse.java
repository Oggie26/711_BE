package com.example._1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private BigDecimal totalRevenue;
    private long totalOrders;
    private long totalUsers;
    private long totalProducts;
    private BigDecimal revenueToday;
    private long ordersToday;
    private long newUsersToday;
}
