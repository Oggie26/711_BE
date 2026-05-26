package com.example._1.repository;

import com.example._1.entity.Order;
import com.example._1.enums.EnumOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = """
        SELECT * FROM orders
        WHERE LOWER(order_code) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """,
            countQuery = """
        SELECT COUNT(*) FROM orders
        WHERE LOWER(order_code) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """,
            nativeQuery = true)
    Page<Order> searchByKeywordNative(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.status = :status AND o.isDeleted = false")
    BigDecimal sumTotalPriceByStatusAndIsDeletedFalse(@Param("status") EnumOrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.status = :status AND o.orderDate >= :startDate AND o.orderDate <= :endDate AND o.isDeleted = false")
    BigDecimal sumTotalPriceByStatusAndOrderDateBetweenAndIsDeletedFalse(@Param("status") EnumOrderStatus status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    long countByIsDeletedFalse();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :startDate AND o.orderDate <= :endDate AND o.isDeleted = false")
    long countByOrderDateBetweenAndIsDeletedFalse(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.orderDate >= :startDate AND o.orderDate <= :endDate AND o.isDeleted = false")
    List<Order> findByStatusAndOrderDateBetweenAndIsDeletedFalse(@Param("status") EnumOrderStatus status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
