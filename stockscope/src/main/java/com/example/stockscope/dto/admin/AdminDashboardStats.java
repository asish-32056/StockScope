package com.example.stockscope.dto.admin;

import lombok.Data;
import java.util.List;

@Data
public class AdminDashboardStats {
    private UserStats userStats;
    private SystemStats systemStats;
    private List<RecentActivity> recentActivities;
    private StockStats stockStats;

    @Data
    public static class UserStats {
        private long totalUsers;
        private long activeUsers;
        private long newUsersToday;
        private long totalAdmins;
        private long unverifiedUsers;
        private long disabledUsers;
        private long activeThisWeek;
    }

    @Data
    public static class SystemStats {
        private String serverStatus;
        private double systemLoad;
        private long totalRequests;
        private int activeUserSessions;
        private double cpuUsage;
        private long memoryUsage;
        private int errorCount;
    }

    @Data
    public static class RecentActivity {
        private String userId;
        private String userName;
        private String action;
        private String description;
        private String timestamp;
        private String ipAddress;
    }

    @Data
    public static class StockStats {
        private long totalPortfolios;
        private long activeStockWatchers;
        private long totalTransactions;
        private double totalPortfolioValue;
    }
}