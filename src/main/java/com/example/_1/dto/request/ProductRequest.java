package com.example._1.dto.request;

import com.example._1.enums.EnumStatus;
import com.example._1.enums.EnumUnit;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price must be at least 0")
    private BigDecimal price;

    private BigDecimal weight;

    @Enumerated(EnumType.STRING)
    private EnumUnit unit;

    @NotNull
    private String thumbnail;

    @NotNull
    @Min(value = 0)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    private EnumStatus status;

    @NotNull
    private Long categoryId;

    List<ImageRequest> image;
}
