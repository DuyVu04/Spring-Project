package com.example.spring_project.controller;

import com.example.spring_project.dto.request.LessonResourceRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.LessonResourceResponse;
import com.example.spring_project.service.LessonResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lessons/{lessonId}/resources")
@RequiredArgsConstructor
public class LessonResourceController {

    private final LessonResourceService lessonResourceService;

    @PostMapping
    ApiResponse<LessonResourceResponse> addResource(
            @PathVariable UUID lessonId,
            @RequestBody LessonResourceRequest request
    ) {
        request.setLessonId(lessonId);
        return ApiResponse.<LessonResourceResponse>builder()
                .result(lessonResourceService.addResource(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<LessonResourceResponse>> getResourcesByLesson(@PathVariable UUID lessonId) {
        return ApiResponse.<List<LessonResourceResponse>>builder()
                .result(lessonResourceService.getResourcesByLesson(lessonId))
                .build();
    }

    @DeleteMapping("/{resourceId}")
    ApiResponse<Void> deleteResource(@PathVariable UUID resourceId) {
        lessonResourceService.deleteResource(resourceId);
        return ApiResponse.<Void>builder().build();
    }
}
