package com.example.stockscope.repository;

import com.example.stockscope.model.User;
import com.example.stockscope.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByEnabled(boolean enabled);

    long countByCreatedAtAfter(LocalDateTime date);

    Page<User> findByNameContainingOrEmailContaining(
            String name,
            String email,
            Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :date AND u.enabled = true")
    List<User> findInactiveUsers(@Param("date") LocalDateTime date);

    @Query("SELECT u FROM User u WHERE u.emailVerified = false AND u.createdAt < :date")
    List<User> findUnverifiedUsers(@Param("date") LocalDateTime date);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") Role role);

    @Query("SELECT u FROM User u WHERE u.enabled = true AND u.emailVerified = true")
    Page<User> findActiveVerifiedUsers(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role = :role AND (u.name LIKE %:search% OR u.email LIKE %:search%)")
    Page<User> findByRoleAndSearch(@Param("role") Role role, @Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate AND u.createdAt <= :endDate")
    long countUsersCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}