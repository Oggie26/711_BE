package com.example._1.controller;

import com.example._1.dto.request.CategoryRequest;
import com.example._1.dto.response.ApiResponse;
import com.example._1.dto.response.CategoryResponse;
import com.example._1.dto.response.PageResponse;
import com.example._1.service.interfaces.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category Controller")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all categories")
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Categories retrieved successfully")
                .data(categoryService.getAllCategories())
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ApiResponse.<CategoryResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Category retrieved successfully")
                .data(categoryService.getCategoryById(id))
                .build();
    }

    @GetMapping("/by-name")
    @Operation(summary = "Get categories by name")
    public ApiResponse<List<CategoryResponse>> getByName(
            @RequestParam String name) {

        return ApiResponse.<List<CategoryResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Categories filtered by name successfully")
                .data(categoryService.getAllCategoriesByCategoryName(name))
                .build();
    }

    @PostMapping
    @Operation(summary = "Create a new category (Admin only)")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request) {

        return ApiResponse.<CategoryResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Category created successfully")
                .data(categoryService.createCategory(request))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryResponse> updateCategory(
            @Valid @RequestBody CategoryRequest request,
            @PathVariable Long id) {

        return ApiResponse.<CategoryResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Category updated successfully")
                .data(categoryService.updateCategory(request, id))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {

        categoryService.deleteCategory(id);

        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Category deleted successfully")
                .data(null)
                .build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search categories with pagination")
    public ApiResponse<PageResponse<CategoryResponse>> searchCategories(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.<PageResponse<CategoryResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Search categories successfully")
                .data(categoryService.searchCategories(keyword, page, size))
                .build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update category status (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> updateStatus(@PathVariable Long id) {

        categoryService.updateStatus(id);

        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Category status updated successfully")
                .data(null)
                .build();
    }
}