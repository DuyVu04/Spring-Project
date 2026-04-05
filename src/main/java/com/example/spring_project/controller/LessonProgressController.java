package com.example.spring_project.controller;

import com.example.spring_project.dto.request.LessonProgressRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.LessonProgressResponse;
import com.example.spring_project.service.LessonProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lesson-progress")
@RequiredArgsConstructor
public class LessonProgressController {

    private final LessonProgressService lessonProgressService;

    @PutMapping
    ApiResponse<LessonProgressResponse> updateProgress(@RequestBody LessonProgressRequest request) {
        return ApiResponse.<LessonProgressResponse>builder()
                .result(lessonProgressService.updateProgress(request))
                .build();
    }

    @GetMapping("/enrollment/{enrollmentId}")
    ApiResponse<List<LessonProgressResponse>> getProgressByEnrollment(@PathVariable UUID enrollmentId) {
        return ApiResponse.<List<LessonProgressResponse>>builder()
                .result(lessonProgressService.getProgressByEnrollment(enrollmentId))
                .build();
    }
}
