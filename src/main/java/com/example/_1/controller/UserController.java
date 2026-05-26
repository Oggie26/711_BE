package com.example._1.controller;

import com.example._1.dto.response.ApiResponse;
import com.example._1.dto.response.PageResponse;
import com.example._1.dto.response.UserResponse;
import com.example._1.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin user theo ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> getUserById(@PathVariable UUID id) {

        return ApiResponse.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User retrieved successfully")
                .data(userService.getUserById(id))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách user")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Object> getAllUsers() {

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Users retrieved successfully")
                .data(userService.getAllUsers())
                .build();
    }

    @GetMapping("/profile")
    @Operation(summary = "Thông tin cá nhân")
    public ApiResponse<Object> profile() {
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Users retrieved successfully")
                .data(userService.profile())
                .build();
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm user")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<UserResponse>> searchUser(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ApiResponse.<PageResponse<UserResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Search user successfully")
                .data(userService.searchUser(keyword, page, size))
                .build();
    }
}