package com.example.spring_project.dto.response;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseReviewResponse {

    private UUID id;

    private Integer rating;

    private String comment;

    private Long userId;

    private String username;

    private UUID courseId;

    private Instant createdAt;
}