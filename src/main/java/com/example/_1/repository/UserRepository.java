package com.example._1.repository;

import com.example._1.entity.Category;
import com.example._1.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u FROM User u WHERE u.account.email = :email")
    Optional<User> findByAccountEmail(@Param("email") String email);

    Optional<User> findByIdAndIsDeletedFalse(UUID id);


    @Query(value = """
    SELECT * FROM users
    WHERE is_deleted = false
      AND (
            LOWER(full_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
    """,
            countQuery = """
    SELECT COUNT(*) FROM users
    WHERE is_deleted = false
      AND (
            LOWER(full_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
    """,
            nativeQuery = true)
    Page<User> searchByKeywordNative(@Param("keyword") String keyword, Pageable pageable);
}
