package com.example._1.service.interfaces;

import com.example._1.dto.request.CategoryRequest;
import com.example._1.dto.response.CategoryResponse;
import com.example._1.dto.response.PageResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest categoryRequest);
    CategoryResponse updateCategory(CategoryRequest categoryRequest, Long id);
    CategoryResponse getCategoryById(Long id);
    List<CategoryResponse> getAllCategories();
    List<CategoryResponse> getAllCategoriesByCategoryName(String categoryName);
    PageResponse<CategoryResponse> searchCategories(String request, int page, int size);
    void deleteCategory(Long id);
    void updateStatus(Long id);

}
