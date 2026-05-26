package com.example._1.service.interfaces;

import com.example._1.dto.request.AuthRequest;
import com.example._1.dto.request.RegisterRequest;
import com.example._1.dto.response.AuthResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Service;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    AuthResponse register(RegisterRequest request);

}
