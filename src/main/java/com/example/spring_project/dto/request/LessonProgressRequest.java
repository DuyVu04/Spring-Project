package com.example.spring_project.dto.request;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonProgressRequest {

    private UUID enrollmentId;

    private UUID lessonId;

    private Integer watchedDuration;

    private Boolean completed;
}