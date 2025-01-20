package com.example.stockscope.dto.admin;

import java.time.LocalDateTime;

public class UserManagementDTO {
    private String id;
    private String name;
    private String email;
    private String role;
    private boolean enabled;
    private boolean emailVerified;
    private String profilePicture;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int totalPortfolios;
    private String status;
    private long activityCount;

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public int getTotalPortfolios() {
        return totalPortfolios;
    }

    public String getStatus() {
        return status;
    }

    public long getActivityCount() {
        return activityCount;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setTotalPortfolios(int totalPortfolios) {
        this.totalPortfolios = totalPortfolios;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setActivityCount(long activityCount) {
        this.activityCount = activityCount;
    }

    // Helper method to get user status
    public String getStatus() {
        if (!enabled) {
            return "DISABLED";
        }
        if (!emailVerified) {
            return "UNVERIFIED";
        }
        if (lastLogin == null) {
            return "NEVER_LOGGED_IN";
        }
        return "ACTIVE";
    }
}