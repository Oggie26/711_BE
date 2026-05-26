package com.example._1.service;

import com.example._1.dto.response.DashboardStatsResponse;
import com.example._1.dto.response.DaySalesResponse;
import com.example._1.dto.response.MonthSalesResponse;
import com.example._1.dto.response.UserCountResponse;
import com.example._1.entity.Order;
import com.example._1.enums.EnumOrderStatus;
import com.example._1.enums.EnumRole;
import com.example._1.repository.AccountRepository;
import com.example._1.repository.OrderRepository;
import com.example._1.repository.ProductRepository;
import com.example._1.service.interfaces.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        BigDecimal totalRevenue = orderRepository.sumTotalPriceByStatusAndIsDeletedFalse(EnumOrderStatus.PAYMENT_SUCCESS);
        long totalOrders = orderRepository.countByIsDeletedFalse();
        long totalUsers = accountRepository.countByRoleAndIsDeletedFalse(EnumRole.USER);
        long totalProducts = productRepository.countByIsDeletedFalse();

        BigDecimal revenueToday = orderRepository.sumTotalPriceByStatusAndOrderDateBetweenAndIsDeletedFalse(
                EnumOrderStatus.PAYMENT_SUCCESS, startOfDay, endOfDay);
        long ordersToday = orderRepository.countByOrderDateBetweenAndIsDeletedFalse(startOfDay, endOfDay);
        long newUsersToday = accountRepository.countByRoleAndCreatedAtBetweenAndIsDeletedFalse(
                EnumRole.USER, startOfDay, endOfDay);

        return DashboardStatsResponse.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .revenueToday(revenueToday)
                .ordersToday(ordersToday)
                .newUsersToday(newUsersToday)
                .build();
    }

    @Override
    public List<DaySalesResponse> getWeeklySales() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = today.atTime(LocalTime.MAX);

        List<Order> successfulOrders = orderRepository.findByStatusAndOrderDateBetweenAndIsDeletedFalse(
                EnumOrderStatus.PAYMENT_SUCCESS, startDateTime, endDateTime);

        Map<LocalDate, List<Order>> ordersByDate = successfulOrders.stream()
                .filter(order -> order.getOrderDate() != null)
                .collect(Collectors.groupingBy(order -> order.getOrderDate().toLocalDate()));

        List<DaySalesResponse> weeklySales = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            List<Order> dailyOrders = ordersByDate.getOrDefault(date, new ArrayList<>());

            BigDecimal dailyRevenue = dailyOrders.stream()
                    .map(Order::getTotalPrice)
                    .filter(price -> price != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            weeklySales.add(DaySalesResponse.builder()
                    .date(date)
                    .dayOfWeek(dayOfWeek)
                    .revenue(dailyRevenue)
                    .orderCount(dailyOrders.size())
                    .build());
        }

        return weeklySales;
    }

    @Override
    public UserCountResponse getTotalUser() {
        long totalUsers = accountRepository.countByRoleAndIsDeletedFalse(EnumRole.USER);
        return UserCountResponse.builder()
                .totalUsers(totalUsers)
                .build();
    }

    @Override
    public List<MonthSalesResponse> getMonthlySales() {
        int currentYear = LocalDate.now().getYear();
        LocalDateTime startDateTime = LocalDateTime.of(currentYear, 1, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(currentYear, 12, 31, 23, 59, 59, 999999999);

        List<Order> successfulOrders = orderRepository.findByStatusAndOrderDateBetweenAndIsDeletedFalse(
                EnumOrderStatus.PAYMENT_SUCCESS, startDateTime, endDateTime);

        Map<Integer, List<Order>> ordersByMonth = successfulOrders.stream()
                .filter(order -> order.getOrderDate() != null)
                .collect(Collectors.groupingBy(order -> order.getOrderDate().getMonthValue()));

        List<MonthSalesResponse> monthlySales = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            List<Order> monthlyOrders = ordersByMonth.getOrDefault(i, new ArrayList<>());

            BigDecimal monthlyRevenue = monthlyOrders.stream()
                    .map(Order::getTotalPrice)
                    .filter(price -> price != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String monthName = java.time.Month.of(i).getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            monthlySales.add(MonthSalesResponse.builder()
                    .month(i)
                    .monthName(monthName)
                    .revenue(monthlyRevenue)
                    .orderCount(monthlyOrders.size())
                    .build());
        }

        return monthlySales;
    }
}
