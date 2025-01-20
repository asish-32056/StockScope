package com.example.stockscope.controller;

import com.example.stockscope.dto.ApiResponse;
import com.example.stockscope.dto.admin.AdminDashboardStats;
import com.example.stockscope.dto.admin.UserManagementDTO;
import com.example.stockscope.model.ActivityLog;
import com.example.stockscope.model.User;
import com.example.stockscope.service.AdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "${app.cors.allowed-origins}", allowCredentials = "true")
@Validated
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardStats>> getDashboardStats() {
        logger.info("Fetching dashboard statistics");
        return ResponseEntity.ok(ApiResponse.success(adminService.getDashboardStats()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserManagementDTO>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        logger.info("Fetching users list. Page: {}, Size: {}, Search: {}", page, size, search);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(adminService.getUsers(pageable, search)));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserManagementDTO>> getUserDetails(
            @PathVariable @Valid @Size(min = 36, max = 36) String userId) {
        logger.info("Fetching user details for ID: {}", userId);
        return ResponseEntity.ok(ApiResponse.success(adminService.getUserDetails(userId)));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable @Valid @Size(min = 36, max = 36) String userId,
            @Valid @RequestBody User updatedUser) {
        logger.info("Updating user with ID: {}", userId);
        return ResponseEntity.ok(ApiResponse.success(adminService.updateUser(userId, updatedUser)));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        logger.info("Deleting user with ID: {}", userId);
        adminService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    @GetMapping("/users/{userId}/activity")
    public ResponseEntity<ApiResponse<Page<ActivityLog>>> getUserActivity(
            @PathVariable @Valid @Size(min = 36, max = 36) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Fetching activity logs for user ID: {}", userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return ResponseEntity.ok(ApiResponse.success(adminService.getUserActivity(userId, pageable)));
    }

    @GetMapping("/activity")
    public ResponseEntity<ApiResponse<Page<ActivityLog>>> getSystemActivity(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Fetching system activity logs");
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllActivities(pageable)));
    }

    @PostMapping("/users/{userId}/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetUserPassword(
            @PathVariable @Valid @Size(min = 36, max = 36) String userId,
            @RequestParam @Valid @Size(min = 8, max = 100) String newPassword) {
        logger.info("Resetting password for user ID: {}", userId);
        adminService.resetUserPassword(userId, newPassword);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }

    @GetMapping("/system/health")
    public ResponseEntity<ApiResponse<AdminDashboardStats.SystemStats>> getSystemHealth() {
        logger.info("Fetching system health status");
        return ResponseEntity.ok(ApiResponse.success(adminService.getSystemHealth()));
    }
}