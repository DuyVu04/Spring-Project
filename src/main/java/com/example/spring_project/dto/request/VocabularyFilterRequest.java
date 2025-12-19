package com.example.spring_project.dto.request;
import com.example.spring_project.enums.WordStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VocabularyFilterRequest {

    private String keyword;

    private WordStatus status;

    private Boolean favorite;
}