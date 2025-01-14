package com.example.stockscope.repository;

import com.example.stockscope.model.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, String> {

    Page<ActivityLog> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);

    List<ActivityLog> findByTimestampBetweenOrderByTimestampDesc(
            LocalDateTime start,
            LocalDateTime end);

    @Query("SELECT a FROM ActivityLog a WHERE a.activityType = :type AND a.timestamp >= :since")
    List<ActivityLog> findByActivityTypeSince(
            @Param("type") ActivityLog.ActivityType type,
            @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE a.userId = :userId AND a.activityType = :type AND a.timestamp >= :since")
    long countUserActivitiesByType(
            @Param("userId") String userId,
            @Param("type") ActivityLog.ActivityType type,
            @Param("since") LocalDateTime since);

    List<ActivityLog> findTop10ByOrderByTimestampDesc();

    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE a.timestamp >= :since")
    long countRecentActivities(@Param("since") LocalDateTime since);
}