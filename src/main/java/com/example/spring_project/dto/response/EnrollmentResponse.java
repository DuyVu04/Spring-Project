package com.example.spring_project.dto.response;

import com.example.spring_project.enums.EnrollmentStatus;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentResponse {

    private UUID id;

    private Long userId;

    private UUID courseId;

    private String courseTitle;

    private String courseThumbnail;

    private Integer completionPercent;

    private EnrollmentStatus status;

    private Instant createdAt;

    private String instructorName;

    private Long totalLessons;
}