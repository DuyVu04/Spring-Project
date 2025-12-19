package com.example.spring_project.dto.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModuleResponse {

    private UUID id;

    private String title;

    private Long orderIndex;

    private List<LessonResponse> lessons;
}