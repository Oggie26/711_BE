package com.example._1.service.interfaces;

import com.example._1.dto.response.DashboardStatsResponse;
import com.example._1.dto.response.DaySalesResponse;
import com.example._1.dto.response.MonthSalesResponse;
import com.example._1.dto.response.UserCountResponse;

import java.util.List;

public interface DashboardService {
    DashboardStatsResponse getDashboardStats();
    List<DaySalesResponse> getWeeklySales();
    List<MonthSalesResponse> getMonthlySales();
    UserCountResponse getTotalUser();
}
