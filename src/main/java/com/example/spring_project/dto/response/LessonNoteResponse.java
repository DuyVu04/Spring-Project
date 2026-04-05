package com.example.spring_project.dto.response;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonNoteResponse {

    private UUID id;

    private String content;

    private Integer videoTimestamp;

    private UUID lessonId;

    private Instant createdAt;

    private Instant updatedAt;
}