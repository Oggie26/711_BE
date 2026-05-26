package com.example._1.controller;

import com.example._1.dto.request.ProductRequest;
import com.example._1.dto.request.ProductUpdateRequest;
import com.example._1.dto.response.ApiResponse;
import com.example._1.dto.response.PageResponse;
import com.example._1.dto.response.ProductResponse;
import com.example._1.service.interfaces.ProductService;
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
@RequestMapping("/api/products")
@Tag(name = "Product Controller")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Create product (Admin only)")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {

        return ApiResponse.<ProductResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Product created successfully")
                .data(productService.createProduct(request))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ProductResponse> updateProduct(
            @Valid @RequestBody ProductUpdateRequest request,
            @PathVariable Long id) {

        return ApiResponse.<ProductResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Product updated successfully")
                .data(productService.updateProduct(request, id))
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {

        return ApiResponse.<ProductResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Product retrieved successfully")
                .data(productService.getProductById(id))
                .build();
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get product by slug")
    public ApiResponse<ProductResponse> getProductBySlug(@PathVariable String slug) {

        return ApiResponse.<ProductResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Product retrieved successfully")
                .data(productService.getProductBySlug(slug))
                .build();
    }

    @GetMapping
    @Operation(summary = "Get all products")
    public ApiResponse<List<ProductResponse>> getAllProducts() {

        return ApiResponse.<List<ProductResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Products retrieved successfully")
                .data(productService.getAllProducts())
                .build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search products")
    public ApiResponse<PageResponse<ProductResponse>> searchProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.<PageResponse<ProductResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Search products successfully")
                .data(productService.searchProducts(keyword, page, size))
                .build();
    }

    @GetMapping("/category/{id}")
    @Operation(summary = "Get products by category")
    public ApiResponse<PageResponse<ProductResponse>> getByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.<PageResponse<ProductResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Products by category retrieved successfully")
                .data(productService.getProductsByCategoryId(id, page, size))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);

        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Product deleted successfully")
                .data(null)
                .build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Toggle product status (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> updateStatus(@PathVariable Long id) {

        productService.updateStatus(id);

        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Product status updated successfully")
                .data(null)
                .build();
    }
}