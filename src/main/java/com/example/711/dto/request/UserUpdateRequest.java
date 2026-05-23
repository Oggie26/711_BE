package com.example._1.dto.request;

import com.example._1.entity.Role;
import lombok.Data;

@Data
public class UserUpdateRequest {
    private String password;
    private Role role;
}
