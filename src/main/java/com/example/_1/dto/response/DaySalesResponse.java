package com.example._1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DaySalesResponse {
    private LocalDate date;
    private String dayOfWeek;
    private BigDecimal revenue;
    private long orderCount;
}
