package com.example._1.controller;

import com.example._1.dto.request.UserRequest;
import com.example._1.response.ApiResponse;
import com.example._1.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
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
    public ApiResponse<String> register(@Valid @RequestBody UserRequest request) {
        String result = authService.register(request.getUsername(), request.getPassword(), request.getRole());
        return ApiResponse.<String>builder()
                .status(HttpStatus.CREATED.value())
                .message("Registration successful")
                .data(result)
                .build();
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        return ApiResponse.<AuthResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Login successful")
                .data(new AuthResponse(token))
                .build();
    }

}
