package com.example._1.controller;

import com.example._1.dto.response.ApiResponse;
import com.example._1.dto.response.DashboardStatsResponse;
import com.example._1.dto.response.DaySalesResponse;
import com.example._1.dto.response.MonthSalesResponse;
import com.example._1.dto.response.UserCountResponse;
import com.example._1.service.interfaces.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard Controller")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Get overall dashboard statistics (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DashboardStatsResponse> getDashboardStats() {
        return ApiResponse.<DashboardStatsResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Dashboard stats retrieved successfully")
                .data(dashboardService.getDashboardStats())
                .build();
    }

    @GetMapping("/weekly-sales")
    @Operation(summary = "Get daily sales over the last 7 days (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<DaySalesResponse>> getWeeklySales() {
        return ApiResponse.<List<DaySalesResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Weekly sales retrieved successfully")
                .data(dashboardService.getWeeklySales())
                .build();
    }

    @GetMapping("/total-users")
    @Operation(summary = "Get total count of active users (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserCountResponse> getTotalUser() {
        return ApiResponse.<UserCountResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Total user count retrieved successfully")
                .data(dashboardService.getTotalUser())
                .build();
    }

    @GetMapping("/monthly-sales")
    @Operation(summary = "Get monthly sales for the current year (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<MonthSalesResponse>> getMonthlySales() {
        return ApiResponse.<List<MonthSalesResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Monthly sales retrieved successfully")
                .data(dashboardService.getMonthlySales())
                .build();
    }
}
