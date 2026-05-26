package com.example._1.service.interfaces;


import com.example._1.dto.request.ProductRequest;
import com.example._1.dto.request.ProductUpdateRequest;
import com.example._1.dto.response.PageResponse;
import com.example._1.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct (ProductRequest productRequest);
    ProductResponse updateProduct(ProductUpdateRequest productRequest, Long id);
    ProductResponse getProductById(Long id);
    PageResponse<ProductResponse> getProductsByCategoryId(Long id, int page, int size);
    ProductResponse getProductBySlug(String slug);
    List<ProductResponse> getAllProducts();
    PageResponse<ProductResponse> searchProducts(String request, int page, int size);
    void deleteProduct(Long id);
    void updateStatus(Long id);
}
