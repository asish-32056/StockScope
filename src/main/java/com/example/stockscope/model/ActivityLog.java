package com.example.stockscope.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "activity_logs")
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String description;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column
    private String ipAddress;

    @Column
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType activityType;

    private String additionalInfo;

    public enum ActivityType {
        LOGIN, LOGOUT, PROFILE_UPDATE, PASSWORD_CHANGE,
        ACCOUNT_CREATION, ACCOUNT_DELETION, SETTINGS_UPDATE,
        ADMIN_ACTION, USER_MANAGEMENT, SYSTEM_UPDATE,
        TOKEN_REFRESH
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}