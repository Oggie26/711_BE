package com.example._1.dto.request;

import com.example._1.enums.EnumStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "Category name cannot be blank")
    private String name;

    @NotBlank(message = "Description name cannot be blank")
    private String description;

    @Enumerated(EnumType.STRING)
    private EnumStatus status;

    @NotBlank(message = "Imager cannot be blank")
    private String image;
}
