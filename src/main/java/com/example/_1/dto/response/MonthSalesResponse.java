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
public class MonthSalesResponse {
    private int month;
    private String monthName;
    private BigDecimal revenue;
    private long orderCount;
}
