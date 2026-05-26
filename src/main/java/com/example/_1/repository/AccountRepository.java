package com.example._1.repository;

import com.example._1.entity.Account;
import com.example._1.enums.EnumRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByEmailAndIsDeletedFalse(String email);
    boolean existsByEmail(String email);
    Optional<Account> findByIdAndIsDeletedFalse(UUID id);

    long countByRoleAndIsDeletedFalse(EnumRole role);

    @Query("SELECT COUNT(a) FROM Account a WHERE a.role = :role AND a.createdAt >= :startDate AND a.createdAt <= :endDate AND a.isDeleted = false")
    long countByRoleAndCreatedAtBetweenAndIsDeletedFalse(@Param("role") EnumRole role, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
