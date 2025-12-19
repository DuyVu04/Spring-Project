package com.example.spring_project.dto.response;

import com.example.spring_project.enums.WordStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VocabularyResponse {

    private UUID id;

    // Flattened word info for convenience
    private WordResponse word;

    private Boolean favorite;

    private WordStatus status;

    private String note;

    private Instant createdAt;
}
