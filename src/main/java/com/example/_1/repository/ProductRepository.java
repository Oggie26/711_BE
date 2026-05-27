package com.example._1.repository;

import com.example._1.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByIdAndIsDeletedFalse(Long id);
    Optional<Product> findBySlugAndIsDeletedFalse(String slug);
    Page<Product> findByCategoryIdAndIsDeletedFalse(Long categoryId, Pageable pageable);
    @Query(value = """
    SELECT * FROM products
    WHERE is_deleted = false
      AND LOWER(name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """,
            countQuery = """
    SELECT COUNT(*) FROM products
    WHERE is_deleted = false
      AND LOWER(name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """,
            nativeQuery = true)
    Page<Product> searchByKeywordNative(
            @Param("keyword") String keyword,
            Pageable pageable
    );
    long countByIsDeletedFalse();
}
