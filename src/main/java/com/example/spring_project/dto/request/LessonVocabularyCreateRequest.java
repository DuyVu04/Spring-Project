package com.example.spring_project.dto.request;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonVocabularyCreateRequest {
    private UUID wordId;
    private Integer displayOrder;
}