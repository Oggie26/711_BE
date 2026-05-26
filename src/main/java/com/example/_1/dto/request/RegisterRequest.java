package com.example._1.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(
            min = 6,
            max = 50,
            message = "Password must be between 6 and 50 characters"
    )
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(
            min = 2,
            max = 100,
            message = "Full name must be between 2 and 100 characters"
    )
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^(0|\\+84)[0-9]{9,10}$",
            message = "Invalid phone number"
    )
    private String phone;

    @Min(value = 0, message = "Point cannot be negative")
    private Integer point = 0;

    @Past(message = "Birthday must be in the past")
    private LocalDate birthday;

    private Boolean gender;
}