package com.example.spring_project.controller;

import com.example.spring_project.dto.request.DiscussionRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.DiscussionResponse;
import com.example.spring_project.service.DiscussionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lessons/{lessonId}/discussions")
@RequiredArgsConstructor
public class DiscussionController {

    private final DiscussionService discussionService;

    @PostMapping
    ApiResponse<DiscussionResponse> createDiscussion(
            @PathVariable UUID lessonId,
            @RequestBody DiscussionRequest request
    ) {
        request.setLessonId(lessonId);
        return ApiResponse.<DiscussionResponse>builder()
                .result(discussionService.createDiscussion(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<DiscussionResponse>> getDiscussionsByLesson(@PathVariable UUID lessonId) {
        return ApiResponse.<List<DiscussionResponse>>builder()
                .result(discussionService.getDiscussionsByLesson(lessonId))
                .build();
    }

    @DeleteMapping("/{discussionId}")
    ApiResponse<Void> deleteDiscussion(@PathVariable UUID discussionId) {
        discussionService.deleteDiscussion(discussionId);
        return ApiResponse.<Void>builder().build();
    }
}
