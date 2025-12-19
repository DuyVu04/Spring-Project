package com.example.spring_project.controller;

import com.example.spring_project.dto.request.LessonRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.LessonResponse;
import com.example.spring_project.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/modules/{moduleId}/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    ApiResponse<LessonResponse> createLesson(
            @PathVariable UUID moduleId,
            @RequestBody LessonRequest lessonRequest
    ) {
        return ApiResponse.<LessonResponse>builder()
                .result(lessonService.createLesson(moduleId, lessonRequest))
                .build();
    }

    @GetMapping
    ApiResponse<List<LessonResponse>> getLessonsByModule(@PathVariable UUID moduleId) {
        return ApiResponse.<List<LessonResponse>>builder()
                .result(lessonService.getLessonsByModule(moduleId))
                .build();
    }

    @GetMapping("/{lessonId}")
    ApiResponse<LessonResponse> getLessonById(@PathVariable UUID lessonId) {
        return ApiResponse.<LessonResponse>builder()
                .result(lessonService.getLessonById(lessonId))
                .build();
    }

    @PutMapping("/{lessonId}")
    ApiResponse<LessonResponse> updateLesson(
            @PathVariable UUID lessonId,
            @RequestBody LessonRequest lessonRequest
    ) {
        return ApiResponse.<LessonResponse>builder()
                .result(lessonService.updateLesson(lessonId, lessonRequest))
                .build();
    }

    @DeleteMapping("/{lessonId}")
    ApiResponse<Void> deleteLesson(@PathVariable UUID lessonId) {
        lessonService.deleteLesson(lessonId);
        return ApiResponse.<Void>builder().build();
    }
}
