package com.example._1.repository;

import com.example._1.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    @Modifying
    @Query("DELETE FROM ProductImage p WHERE p.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}
