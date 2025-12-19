package com.example.spring_project.dto.response;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonResponse {

    private UUID id;

    private String title;

    private String videoUrl;

    private Long duration;

    private Integer orderIndex;

    private Boolean preview;

    private Boolean locked;
}