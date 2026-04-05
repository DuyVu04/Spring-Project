package com.example.spring_project.controller;

import com.example.spring_project.dto.request.LessonVocabularyCreateRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.LessonVocabularyResponse;
import com.example.spring_project.service.LessonVocabularyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lessons/{lessonId}/vocabulary")
@RequiredArgsConstructor
public class LessonVocabularyController {
    private final LessonVocabularyService lessonVocabularyService;

    @GetMapping
    ApiResponse<List<LessonVocabularyResponse>> getLessonVocabulary(@PathVariable UUID lessonId) {
        return ApiResponse.<List<LessonVocabularyResponse>>builder()
                .result(lessonVocabularyService.getLessonVocabulary(lessonId))
                .build();
    }

    @PostMapping
    ApiResponse<LessonVocabularyResponse> addLessonVocabulary(
            @PathVariable UUID lessonId,
            @RequestBody LessonVocabularyCreateRequest request
    ) {
        return ApiResponse.<LessonVocabularyResponse>builder()
                .result(lessonVocabularyService.addLessonVocabulary(lessonId, request))
                .build();
    }

    @DeleteMapping("/{lessonVocabularyId}")
    ApiResponse<Void> deleteLessonVocabulary(
            @PathVariable UUID lessonId,
            @PathVariable UUID lessonVocabularyId
    ) {
        lessonVocabularyService.deleteLessonVocabulary(lessonId, lessonVocabularyId);
        return ApiResponse.<Void>builder().build();
    }
}