package com.example.spring_project.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonProgressResponse {

    private UUID id;

    private UUID lessonId;

    private String lessonTitle;

    private Integer watchedDuration;

    private Boolean completed;

    private LocalDateTime lastWatchedAt;
}