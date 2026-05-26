package com.example._1.repository;

import com.example._1.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByIdAndIsDeletedFalse(Long id);
    Optional<Category> findByNameAndIsDeletedFalse (String name);
    @Query(value = """
        SELECT * FROM categories 
        WHERE is_deleted = false 
          AND (LOWER(name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
               OR LOWER(description) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """,
            countQuery = """
        SELECT COUNT(*) FROM categories 
        WHERE is_deleted = false 
          AND (LOWER(name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
               OR LOWER(description) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """,
            nativeQuery = true)
    Page<Category> searchByKeywordNative(@Param("keyword") String keyword, PageRequest pageable);
    List<Category> findByNameContainingIgnoreCase(String name);
}
