package com.example.stockscope.dto.admin;

import java.util.List;

public class AdminDashboardStats {
    private UserStats userStats;
    private SystemStats systemStats;
    private List<RecentActivity> recentActivities;

    // Getters
    public UserStats getUserStats() {
        return userStats;
    }

    public SystemStats getSystemStats() {
        return systemStats;
    }

    public List<RecentActivity> getRecentActivities() {
        return recentActivities;
    }

    // Setters
    public void setUserStats(UserStats userStats) {
        this.userStats = userStats;
    }

    public void setSystemStats(SystemStats systemStats) {
        this.systemStats = systemStats;
    }

    public void setRecentActivities(List<RecentActivity> recentActivities) {
        this.recentActivities = recentActivities;
    }

    public static class UserStats {
        private long totalUsers;
        private long activeUsers;
        private long newUsers;

        // Getters
        public long getTotalUsers() {
            return totalUsers;
        }

        public long getActiveUsers() {
            return activeUsers;
        }

        public long getNewUsers() {
            return newUsers;
        }

        // Setters
        public void setTotalUsers(long totalUsers) {
            this.totalUsers = totalUsers;
        }

        public void setActiveUsers(long activeUsers) {
            this.activeUsers = activeUsers;
        }

        public void setNewUsers(long newUsers) {
            this.newUsers = newUsers;
        }
    }

    public static class SystemStats {
        private String cpuUsage;
        private String memoryUsage;
        private String diskSpace;
        private boolean healthy;

        // Getters
        public String getCpuUsage() {
            return cpuUsage;
        }

        public String getMemoryUsage() {
            return memoryUsage;
        }

        public String getDiskSpace() {
            return diskSpace;
        }

        public boolean isHealthy() {
            return healthy;
        }

        // Setters
        public void setCpuUsage(String cpuUsage) {
            this.cpuUsage = cpuUsage;
        }

        public void setMemoryUsage(String memoryUsage) {
            this.memoryUsage = memoryUsage;
        }

        public void setDiskSpace(String diskSpace) {
            this.diskSpace = diskSpace;
        }

        public void setHealthy(boolean healthy) {
            this.healthy = healthy;
        }
    }

    public static class RecentActivity {
        private String userId;
        private String action;
        private String description;
        private String timestamp;

        // Getters
        public String getUserId() {
            return userId;
        }

        public String getAction() {
            return action;
        }

        public String getDescription() {
            return description;
        }

        public String getTimestamp() {
            return timestamp;
        }

        // Setters
        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}