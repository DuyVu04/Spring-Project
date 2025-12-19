package com.example.spring_project.dto.request;

import com.example.spring_project.enums.ResourceType;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonResourceRequest {

    private String title;

    private String fileUrl;

    private ResourceType type;

    private UUID lessonId;
}