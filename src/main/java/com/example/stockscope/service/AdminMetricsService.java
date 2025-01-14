package com.example.stockscope.service;

import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminMetricsService {

    private final OperatingSystemMXBean osBean;
    private final MemoryMXBean memoryBean;

    public AdminMetricsService() {
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
        this.memoryBean = ManagementFactory.getMemoryMXBean();
    }

    public Map<String, Object> getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // CPU metrics
        metrics.put("systemLoad", osBean.getSystemLoadAverage());
        metrics.put("availableProcessors", osBean.getAvailableProcessors());

        // Memory metrics
        long usedHeapMemory = memoryBean.getHeapMemoryUsage().getUsed();
        long maxHeapMemory = memoryBean.getHeapMemoryUsage().getMax();

        metrics.put("usedMemory", usedHeapMemory / (1024 * 1024)); // Convert to MB
        metrics.put("maxMemory", maxHeapMemory / (1024 * 1024)); // Convert to MB
        metrics.put("memoryUtilization", (double) usedHeapMemory / maxHeapMemory * 100);

        // Runtime metrics
        Runtime runtime = Runtime.getRuntime();
        metrics.put("freeMemory", runtime.freeMemory() / (1024 * 1024));
        metrics.put("totalMemory", runtime.totalMemory() / (1024 * 1024));

        return metrics;
    }

    public boolean isSystemHealthy() {
        double memoryUtilization = (double) memoryBean.getHeapMemoryUsage().getUsed() /
                memoryBean.getHeapMemoryUsage().getMax();
        double systemLoad = osBean.getSystemLoadAverage();

        return memoryUtilization < 0.9 && systemLoad < 0.8;
    }

    public Map<String, String> getSystemStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("status", isSystemHealthy() ? "HEALTHY" : "DEGRADED");
        status.put("uptime", String.valueOf(ManagementFactory.getRuntimeMXBean().getUptime()));
        return status;
    }
}