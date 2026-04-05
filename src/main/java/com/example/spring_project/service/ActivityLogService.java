package com.example.spring_project.service;

import com.example.spring_project.entity.ActivityLog;
import com.example.spring_project.entity.User;
import com.example.spring_project.enums.ActionType;
import com.example.spring_project.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityLogService {
    private final ActivityLogRepository activityLogRepository;

    @Transactional
    public void logActivity(User user, ActionType type, String description) {
        activityLogRepository.save(ActivityLog.builder()
                .user(user)
                .actionType(type)
                .description(description)
                .build());
    }

    @Transactional
    public void logActivityWithDetails(User user, ActionType type, String description, String details) {
        activityLogRepository.save(ActivityLog.builder()
                .user(user)
                .actionType(type)
                .description(description)
                .details(details)
                .build());
    }
}
