package com.example.stockscope.service.impl;

import com.example.stockscope.model.ActivityLog;
import com.example.stockscope.repository.ActivityLogRepository;
import com.example.stockscope.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Autowired
    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Override
    public void logActivity(String userId, ActivityLog.ActivityType type, String description, String additionalInfo) {
        ActivityLog log = new ActivityLog();
        log.setUserId(userId);
        log.setActivityType(type);
        log.setDescription(description);
        log.setAdditionalInfo(additionalInfo);
        log.setTimestamp(LocalDateTime.now());
        activityLogRepository.save(log);
    }
}