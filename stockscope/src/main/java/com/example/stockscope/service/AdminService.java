package com.example.stockscope.service;

import com.example.stockscope.dto.admin.AdminDashboardStats;
import com.example.stockscope.dto.admin.UserManagementDTO;
import com.example.stockscope.model.ActivityLog;
import com.example.stockscope.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminService {
    AdminDashboardStats getDashboardStats();

    Page<UserManagementDTO> getUsers(Pageable pageable, String search);

    UserManagementDTO getUserDetails(String userId);

    UserManagementDTO toggleUserStatus(String userId);

    void deleteUser(String userId);

    Page<ActivityLog> getUserActivity(String userId, Pageable pageable);

    List<ActivityLog> getSystemActivity(LocalDateTime startDate, LocalDateTime endDate);

    Page<ActivityLog> getAllActivities(Pageable pageable);

    void logActivity(String userId, String action, String description);

    void resetUserPassword(String userId, String newPassword);

    AdminDashboardStats.SystemStats getSystemHealth();
}