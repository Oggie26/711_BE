package com.example._1.controller;

import com.example._1.dto.request.AuthRequest;
import com.example._1.dto.request.RegisterRequest;
import com.example._1.dto.response.ApiResponse;
import com.example._1.dto.response.AuthResponse;
import com.example._1.service.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth Controller")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new account")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse result = authService.register(request);
        return ApiResponse.<AuthResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Registration successful")
                .data(result)
                .build();
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ApiResponse.<AuthResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Login successful")
                .data(authResponse)
                .build();
    }

}
