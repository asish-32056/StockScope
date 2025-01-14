package com.example.stockscope.service;

import com.example.stockscope.model.ActivityLog;
import com.example.stockscope.repository.ActivityLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class AdminAuditService {

    private final ActivityLogRepository activityLogRepository;
    private final HttpServletRequest request;
    private final Map<String, Integer> operationCounter = new ConcurrentHashMap<>();

    @Autowired
    public AdminAuditService(ActivityLogRepository activityLogRepository, HttpServletRequest request) {
        this.activityLogRepository = activityLogRepository;
        this.request = request;
    }

    public void logAdminAction(String adminId, String targetUserId, String action, String details) {
        ActivityLog log = new ActivityLog();
        log.setUserId(adminId);
        log.setAction("ADMIN_" + action);
        log.setDescription(String.format("Admin action on user %s: %s - %s",
                targetUserId, action, details));
        log.setTimestamp(LocalDateTime.now());
        log.setIpAddress(getClientIp());
        log.setUserAgent(request.getHeader("User-Agent"));
        log.setActivityType(ActivityLog.ActivityType.ADMIN_ACTION);

        activityLogRepository.save(log);
        incrementOperationCount(action);
    }

    public void logSystemAction(String adminId, String action, String details) {
        ActivityLog log = new ActivityLog();
        log.setUserId(adminId);
        log.setAction("SYSTEM_" + action);
        log.setDescription(String.format("System action: %s - %s", action, details));
        log.setTimestamp(LocalDateTime.now());
        log.setIpAddress(getClientIp());
        log.setUserAgent(request.getHeader("User-Agent"));
        log.setActivityType(ActivityLog.ActivityType.SYSTEM_UPDATE);

        activityLogRepository.save(log);
        incrementOperationCount(action);
    }

    public Map<String, Integer> getOperationMetrics() {
        return new ConcurrentHashMap<>(operationCounter);
    }

    private void incrementOperationCount(String operation) {
        operationCounter.compute(operation, (k, v) -> (v == null) ? 1 : v + 1);
    }

    private String getClientIp() {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}