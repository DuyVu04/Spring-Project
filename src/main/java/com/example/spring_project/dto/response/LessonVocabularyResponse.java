package com.example.spring_project.dto.response;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonVocabularyResponse {
    private UUID id;
    private UUID wordId;
    private String word;
    private String pronunciation;
    private String definition;
    private String definitionVi;
    private String exampleSentence;
    private String exampleSentenceVi;
    private String level;
    private Integer displayOrder;
}