package com.example._1.service;

import com.example._1.dto.request.ProductRequest;
import com.example._1.dto.request.ProductUpdateRequest;
import com.example._1.dto.response.PageResponse;
import com.example._1.dto.response.ProductImageResponse;
import com.example._1.dto.response.ProductResponse;
import com.example._1.entity.Category;
import com.example._1.entity.Product;
import com.example._1.entity.ProductImage;
import com.example._1.enums.EnumStatus;
import com.example._1.enums.ErrorCode;
import com.example._1.exception.AppException;
import com.example._1.repository.CategoryRepository;
import com.example._1.repository.ProductImageRepository;
import com.example._1.repository.ProductRepository;
import com.example._1.service.interfaces.ProductService;
import com.example._1.util.SlugUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {

        Category category = categoryRepository.findByIdAndIsDeletedFalse(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        String slug = SlugUtil.toSlug(category.getName() + "-" + request.getName());

        Product product = Product.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .price(request.getPrice())
                .weight(request.getWeight())
                .unit(request.getUnit())
                .thumbnail(request.getThumbnail())
                .stock(request.getStock())
                .status(request.getStatus())
                .category(category)
                .build();

        if (request.getImage() != null && !request.getImage().isEmpty()) {

            List<ProductImage> images = request.getImage().stream()
                    .map(img -> ProductImage.builder()
                            .url(img.getImage())
                            .product(product)
                            .build())
                    .toList();

            product.setProductImages(images);
        }
        productRepository.save(product);
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(ProductUpdateRequest request, Long id) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Category category = categoryRepository.findByIdAndIsDeletedFalse(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        String newSlug = SlugUtil.toSlug(category.getName() + "-" + request.getName());
        product.setSlug(newSlug);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setWeight(request.getWeight());
        product.setUnit(request.getUnit());
        product.setStock(request.getStock());
        product.setStatus(request.getStatus());
        product.setThumbnail(request.getThumbnail());
        product.setCategory(category);
        product.setBarcode(request.getBarcode());

        productImageRepository.deleteByProductId(id);

        product.getProductImages().clear();

        if (request.getImage() != null) {
            List<ProductImage> newImages = request.getImage().stream()
                    .map(imgReq -> ProductImage.builder()
                            .url(imgReq.getImage()) // Key phải là .getImage() khớp ImageRequest
                            .product(product)
                            .build())
                    .toList();

            product.getProductImages().addAll(newImages);
        }

        // 5. Lưu sản phẩm
        productRepository.save(product);

        return mapToResponse(product);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return mapToResponse(product);
    }

    @Override
    public PageResponse<ProductResponse> getProductsByCategoryId(Long id, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Product> result = productRepository.findByCategoryIdAndIsDeletedFalse(id, pageable);

        List<ProductResponse> data = result.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return PageResponse.<ProductResponse>builder()
                .data(data)
                .page(page)
                .size(size)
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();
    }

    @Override
    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlugAndIsDeletedFalse(slug)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return mapToResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .filter(product -> !product.isDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PageResponse<ProductResponse> searchProducts(String request, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.searchByKeywordNative(request, pageable);
        List<ProductResponse> data = productPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return PageResponse.<ProductResponse>builder()
                .data(data)
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        product.setDeleted(true);
        product.setStatus(EnumStatus.INACTIVE);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void updateStatus(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if (product.getStatus().equals(EnumStatus.ACTIVE)){
            product.setStatus(EnumStatus.INACTIVE);
        }else{
            product.setStatus(EnumStatus.ACTIVE);
        }
        productRepository.save(product);
    }

    private ProductResponse mapToResponse(Product product) {

        List<ProductImageResponse> images = product.getProductImages() != null
                ? product.getProductImages().stream()
                .map(this::mapImage)
                .toList()
                : List.of();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .barcode(product.getBarcode())
                .description(product.getDescription())
                .price(product.getPrice())
                .weight(product.getWeight())
                .unit(product.getUnit())
                .stock(product.getStock())
                .thumbnail(product.getThumbnail())
                .status(product.getStatus())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .images(images)
                .build();
    }

    private ProductImageResponse mapImage(ProductImage image) {
        return ProductImageResponse.builder()
                .id(image.getId())
                .url(image.getUrl())
                .build();
    }
}