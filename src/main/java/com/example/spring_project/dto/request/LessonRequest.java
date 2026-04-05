package com.example.spring_project.dto.request;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonRequest {

    private String title;

    private String videoUrl;

    private Long duration;

    private Long orderIndex;

    private Boolean preview;

    private Boolean locked;

    private UUID moduleId;
}
