package com.example._1.dto.response;

import com.example._1.enums.EnumStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private EnumStatus status;
    private String image;

}
