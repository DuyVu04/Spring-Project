package com.example.spring_project.dto.request;

import com.example.spring_project.enums.PartOfSpeech;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WordRequest {

    private String word;

    private String pronunciation;

    private PartOfSpeech partOfSpeech;

    private String definition;

    private String exampleSentence;

    private String level;
}
