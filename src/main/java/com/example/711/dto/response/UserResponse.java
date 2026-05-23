package com.example._1.dto.response;

import com.example._1.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String id; // Changed from Long to String to support UUID
    private String username;
    private Role role;
    
    // Profile info
    private String fullName;
    private String phoneNumber;
    private String address;
    private Integer loyaltyPoints;
}
