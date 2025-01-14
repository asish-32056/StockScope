package com.example.stockscope.service;

import com.example.stockscope.model.ActivityLog;

public interface ActivityLogService {
    void logActivity(String userId, ActivityLog.ActivityType type, String description, String additionalInfo);
}