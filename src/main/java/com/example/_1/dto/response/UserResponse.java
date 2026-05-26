package com.example._1.dto.response;

import com.example._1.enums.EnumRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private UUID id;
    private String email;
    private EnumRole role;
    private String fullName;
    private String phone;
    private Boolean gender;
    private LocalDate birthday;
    private Integer point;
}
