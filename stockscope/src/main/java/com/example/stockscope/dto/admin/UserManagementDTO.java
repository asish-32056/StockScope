package com.example.stockscope.dto.admin;

import lombok.Data;
import java.time.LocalDateTime;

@Data
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