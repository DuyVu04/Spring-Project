package com.example.spring_project.dto.request;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonNoteRequest {

    private String content;

    private Integer videoTimestamp;

    private UUID lessonId;
}