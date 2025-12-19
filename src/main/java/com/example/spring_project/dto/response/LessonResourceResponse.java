package com.example.spring_project.dto.response;

import com.example.spring_project.enums.ResourceType;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LessonResourceResponse {

    private UUID id;

    private String title;

    private String fileUrl;

    private ResourceType type;
}