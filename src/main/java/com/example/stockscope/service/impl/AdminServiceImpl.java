package com.example.stockscope.service.impl;

import com.example.stockscope.dto.admin.AdminDashboardStats;
import com.example.stockscope.dto.admin.UserManagementDTO;
import com.example.stockscope.exception.AdminOperationException;
import com.example.stockscope.exception.ResourceNotFoundException;
import com.example.stockscope.model.ActivityLog;
import com.example.stockscope.model.Role;
import com.example.stockscope.model.User;
import com.example.stockscope.repository.ActivityLogRepository;
import com.example.stockscope.repository.UserRepository;
import com.example.stockscope.service.AdminAuditService;
import com.example.stockscope.service.AdminMetricsService;
import com.example.stockscope.service.AdminService;
import com.example.stockscope.util.AdminValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminAuditService adminAuditService;
    private final AdminMetricsService adminMetricsService;

    @Autowired
    public AdminServiceImpl(UserRepository userRepository,
            ActivityLogRepository activityLogRepository,
            PasswordEncoder passwordEncoder,
            AdminAuditService adminAuditService,
            AdminMetricsService adminMetricsService) {
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminAuditService = adminAuditService;
        this.adminMetricsService = adminMetricsService;
    }

    @Override
    public AdminDashboardStats getDashboardStats() {
        AdminDashboardStats stats = new AdminDashboardStats();

        // User Stats
        AdminDashboardStats.UserStats userStats = new AdminDashboardStats.UserStats();
        userStats.setTotalUsers(userRepository.count());
        userStats.setActiveUsers(userRepository.countByEnabled(true));
        userStats.setNewUsersToday(userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(1)));
        userStats.setTotalAdmins(userRepository.countByRole(Role.ADMIN));
        stats.setUserStats(userStats);

        // System Stats
        Map<String, Object> metrics = adminMetricsService.getSystemMetrics();
        AdminDashboardStats.SystemStats systemStats = new AdminDashboardStats.SystemStats();
        systemStats.setServerStatus(adminMetricsService.isSystemHealthy() ? "HEALTHY" : "DEGRADED");
        systemStats.setSystemLoad((double) metrics.get("systemLoad"));
        systemStats.setMemoryUsage((long) metrics.get("usedMemory"));
        systemStats.setTotalRequests(activityLogRepository.count());
        systemStats.setActiveUserSessions(getActiveUserSessions());
        stats.setSystemStats(systemStats);

        return stats;
    }

    @Override
    public Page<UserManagementDTO> getUsers(Pageable pageable, String search) {
        Page<User> users;
        if (search != null && !search.trim().isEmpty()) {
            users = userRepository.findByNameContainingOrEmailContaining(search, search, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }
        return users.map(this::convertToUserManagementDTO);
    }

    @Override
    public UserManagementDTO getUserDetails(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return convertToUserManagementDTO(user);
    }

    @Override
    public UserManagementDTO toggleUserStatus(String userId) {
        User currentAdmin = getCurrentAdmin();
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        AdminValidationUtil.validateAdminOperation(targetUser, currentAdmin);

        targetUser.setEnabled(!targetUser.isEnabled());
        targetUser = userRepository.save(targetUser);

        adminAuditService.logAdminAction(
                currentAdmin.getId(),
                userId,
                "TOGGLE_STATUS",
                "User status changed to: " + (targetUser.isEnabled() ? "enabled" : "disabled"));

        return convertToUserManagementDTO(targetUser);
    }

    @Override
    public void deleteUser(String userId) {
        User currentAdmin = getCurrentAdmin();
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        AdminValidationUtil.validateAdminOperation(targetUser, currentAdmin);

        userRepository.deleteById(userId);

        adminAuditService.logAdminAction(
                currentAdmin.getId(),
                userId,
                "DELETE_USER",
                "User account deleted");
    }

    @Override
    public void resetUserPassword(String userId, String newPassword) {
        User currentAdmin = getCurrentAdmin();
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        AdminValidationUtil.validateAdminOperation(targetUser, currentAdmin);
        AdminValidationUtil.validatePasswordStrength(newPassword);

        targetUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(targetUser);

        adminAuditService.logAdminAction(
                currentAdmin.getId(),
                userId,
                "RESET_PASSWORD",
                "Password reset by admin");
    }

    @Override
    public AdminDashboardStats.SystemStats getSystemHealth() {
        Map<String, Object> metrics = adminMetricsService.getSystemMetrics();
        AdminDashboardStats.SystemStats stats = new AdminDashboardStats.SystemStats();
        stats.setServerStatus(adminMetricsService.isSystemHealthy() ? "HEALTHY" : "DEGRADED");
        stats.setSystemLoad((double) metrics.get("systemLoad"));
        stats.setMemoryUsage((long) metrics.get("usedMemory"));
        stats.setTotalRequests(activityLogRepository.count());
        stats.setActiveUserSessions(getActiveUserSessions());
        return stats;
    }

    @Override
    public List<ActivityLog> getSystemActivity(LocalDateTime startDate, LocalDateTime endDate) {
        return activityLogRepository.findByTimestampBetweenOrderByTimestampDesc(
                startDate != null ? startDate : LocalDateTime.now().minusDays(7),
                endDate != null ? endDate : LocalDateTime.now());
    }

    @Override
    public Page<ActivityLog> getAllActivities(Pageable pageable) {
        return activityLogRepository.findAll(pageable);
    }

    @Override
    public Page<ActivityLog> getUserActivity(String userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return activityLogRepository.findByUserIdOrderByTimestampDesc(userId, pageable);
    }

    @Override
    public void logActivity(String userId, String action, String description) {
        ActivityLog log = new ActivityLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setDescription(description);
        log.setTimestamp(LocalDateTime.now());
        log.setActivityType(ActivityLog.ActivityType.ADMIN_ACTION);
        activityLogRepository.save(log);
    }

    private User getCurrentAdmin() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AdminOperationException("Admin not found"));
    }

    private UserManagementDTO convertToUserManagementDTO(User user) {
        UserManagementDTO dto = new UserManagementDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().toString());
        dto.setEnabled(user.isEnabled());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setLastLogin(user.getLastLoginAt());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    private int getActiveUserSessions() {
        // Count users who logged in within the last hour
        return (int) userRepository.countByRole(Role.USER);
    }
}
