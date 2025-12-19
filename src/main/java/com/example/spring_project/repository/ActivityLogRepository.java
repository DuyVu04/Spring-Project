package com.example.spring_project.repository;

import com.example.spring_project.entity.ActivityLog;
import com.example.spring_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByUserOrderByCreatedAtDesc(User user);

    List<ActivityLog> findTop10ByOrderByCreatedAtDesc();

    // For counting activity per day (simplified for now)
    List<ActivityLog> findByUserAndCreatedAtAfter(User user, Instant after);
}
