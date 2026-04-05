package com.example.spring_project.dto.response;

import com.example.spring_project.enums.CourseStatus;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {

    private UUID id;

    private String title;

    private String description;

    private String thumbnail;

    private String instructorName;

    private String instructorAvatar;

    private String instructorTitle;

    private boolean free;

    private Long totalDuration;

    private Long totalModules;

    private Long totalLessons;

    private Double rating;

    private Long totalEnrolled;

    private CourseStatus status;

    private List<ModuleResponse> modules;

    private Instant createdAt;
}