package com.example.spring_project.dto.request;

import com.example.spring_project.enums.PartOfSpeech;
import com.example.spring_project.enums.WordStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VocabularyRequest {

    // Option 1: add from dictionary (wordId provided)
    private UUID wordId;

    // Option 2: manual entry (wordId is null, fill these fields)
    private String word;
    private String pronunciation;
    private PartOfSpeech partOfSpeech;
    private String definition;
    private String exampleSentence;
    private String level;

    // Notebook metadata
    private Boolean favorite;
    private WordStatus status;
    private String note;
}
