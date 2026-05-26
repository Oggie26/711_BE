package com.example._1.dto.response;

import com.example._1.entity.ProductImage;
import com.example._1.enums.EnumStatus;
import com.example._1.enums.EnumUnit;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String slug;
    private String barcode;
    private String description;
    private BigDecimal price;
    private String categoryName;
    private BigDecimal weight;
    @Enumerated(EnumType.STRING)
    private EnumStatus status;
    private Integer stock;
    private String thumbnail;
    @Enumerated(EnumType.STRING)
    private EnumUnit unit;
    private List<ProductImageResponse> images;
}
