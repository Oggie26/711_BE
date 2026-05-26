package com.example._1.service;

import com.example._1.dto.request.CategoryRequest;
import com.example._1.dto.response.CategoryResponse;
import com.example._1.dto.response.PageResponse;
import com.example._1.entity.Category;
import com.example._1.enums.EnumStatus;
import com.example._1.enums.ErrorCode;
import com.example._1.exception.AppException;
import com.example._1.repository.CategoryRepository;
import com.example._1.service.interfaces.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        if (categoryRepository.findByNameAndIsDeletedFalse(categoryRequest.getName()).isPresent()) {
            throw new AppException(ErrorCode.CATEGORY_NAME_EXISTED);
        }

        Category category = Category.builder()
                .name(categoryRequest.getName())
                .description(categoryRequest.getDescription())
                .image(categoryRequest.getImage())
                .status(EnumStatus.ACTIVE)
                .build();

        categoryRepository.save(category);
        return toCategoryResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(CategoryRequest categoryRequest, Long id) {
        Category category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        categoryRepository.findByNameAndIsDeletedFalse(categoryRequest.getName())
                .filter(existing -> !existing.getId().equals(category.getId()))
                .ifPresent(existing -> {
                    throw new AppException(ErrorCode.CATEGORY_NAME_EXISTED);
                });

        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        category.setImage(categoryRequest.getImage());
        category.setStatus(categoryRequest.getStatus());

        categoryRepository.save(category);
        return toCategoryResponse(category);
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        return toCategoryResponse(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .filter(category -> !category.isDeleted() && category.getStatus().equals(EnumStatus.ACTIVE))
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<CategoryResponse> searchCategories(String keyword, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.searchByKeywordNative(keyword, pageable);

        List<CategoryResponse> data = categoryPage.getContent().stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                data,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages()
        );
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        category.setDeleted(true);
        category.setStatus(EnumStatus.INACTIVE);
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void updateStatus(Long id) {
        Category category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        if (category.getStatus().equals(EnumStatus.ACTIVE)) {
            category.setStatus(EnumStatus.INACTIVE);
        }else {
            category.setDeleted(false);
        }
        categoryRepository.save(category);
    }

    private CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .image(category.getImage())
                .status(category.getStatus())
                .build();
    }
}