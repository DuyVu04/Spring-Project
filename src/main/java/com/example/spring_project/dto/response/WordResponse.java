package com.example.spring_project.dto.response;

import com.example.spring_project.enums.PartOfSpeech;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WordResponse {

    private UUID id;

    private String word;

    private String pronunciation;

    private PartOfSpeech partOfSpeech;

    private String definition;

    private String definitionVi;

    private String exampleSentence;

    private String exampleSentenceVi;

    private String level;
}
