package com.example.spring_project.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VocabularyListResponse {

    private List<VocabularyResponse> words;

    private Integer page;

    private Integer size;

    private Long totalElements;

    private Integer totalPages;
}