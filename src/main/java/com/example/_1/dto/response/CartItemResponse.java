package com.example._1.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    Long productId;
    String productName;
    String thumbnail;
    BigDecimal price;
    int quantity;
    BigDecimal totalItemPrice;
}

