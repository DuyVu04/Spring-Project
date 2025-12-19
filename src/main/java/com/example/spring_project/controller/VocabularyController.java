package com.example.spring_project.controller;

import com.example.spring_project.dto.request.VocabularyFilterRequest;
import com.example.spring_project.dto.request.VocabularyRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.PageResponse;
import com.example.spring_project.dto.response.VocabularyResponse;
import com.example.spring_project.service.VocabularyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/vocabularies")
@RequiredArgsConstructor
public class VocabularyController {

    private final VocabularyService vocabularyService;

    /**
     * Add word to notebook
     * POST /vocabularies
     * Body: { wordId } OR { word, pronunciation, partOfSpeech, definition, ... }
     */
    @PostMapping
    public ApiResponse<VocabularyResponse> addToNotebook(@RequestBody VocabularyRequest request) {
        return ApiResponse.<VocabularyResponse>builder()
                .result(vocabularyService.addToNotebook(request))
                .build();
    }

    /**
     * Get user's notebook with pagination and filter
     * GET /vocabularies?keyword=run&status=TO_LEARN&page=0&size=10
     */
    @GetMapping
    public ApiResponse<PageResponse<VocabularyResponse>> getVocabularies(
            VocabularyFilterRequest filterRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return ApiResponse.<PageResponse<VocabularyResponse>>builder()
                .result(vocabularyService.getAllVocabularies(filterRequest, page, size, sortBy, sortDir))
                .build();
    }

    /**
     * Get single vocabulary entry
     * GET /vocabularies/{vocabularyId}
     */
    @GetMapping("/{vocabularyId}")
    public ApiResponse<VocabularyResponse> getVocabulary(@PathVariable UUID vocabularyId) {
        return ApiResponse.<VocabularyResponse>builder()
                .result(vocabularyService.getVocabulary(vocabularyId))
                .build();
    }

    /**
     * Update notebook entry (note, status, favorite)
     * PUT /vocabularies/{vocabularyId}
     */
    @PutMapping("/{vocabularyId}")
    public ApiResponse<VocabularyResponse> updateVocabulary(
            @PathVariable UUID vocabularyId,
            @RequestBody VocabularyRequest request
    ) {
        return ApiResponse.<VocabularyResponse>builder()
                .result(vocabularyService.updateVocabulary(vocabularyId, request))
                .build();
    }

    /**
     * Remove word from notebook
     * DELETE /vocabularies/{vocabularyId}
     */
    @DeleteMapping("/{vocabularyId}")
    public ApiResponse<Void> deleteVocabulary(@PathVariable UUID vocabularyId) {
        vocabularyService.deleteVocabulary(vocabularyId);
        return ApiResponse.<Void>builder()
                .message("Removed from notebook")
                .build();
    }

    /**
     * Toggle favorite
     * PATCH /vocabularies/{vocabularyId}/favorite
     */
    @PatchMapping("/{vocabularyId}/favorite")
    public ApiResponse<VocabularyResponse> toggleFavorite(@PathVariable UUID vocabularyId) {
        return ApiResponse.<VocabularyResponse>builder()
                .result(vocabularyService.toggleFavorite(vocabularyId))
                .build();
    }

    /**
     * Mark word as mastered
     * PATCH /vocabularies/{vocabularyId}/mastered
     */
    @PatchMapping("/{vocabularyId}/mastered")
    public ApiResponse<VocabularyResponse> markAsMastered(@PathVariable UUID vocabularyId) {
        return ApiResponse.<VocabularyResponse>builder()
                .result(vocabularyService.markAsMastered(vocabularyId))
                .build();
    }
}
