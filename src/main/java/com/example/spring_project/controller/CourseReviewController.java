package com.example.spring_project.controller;

import com.example.spring_project.dto.request.CourseReviewRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.CourseReviewResponse;
import com.example.spring_project.service.CourseReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/courses/{courseId}/reviews")
@RequiredArgsConstructor
public class CourseReviewController {

    private final CourseReviewService courseReviewService;

    @PostMapping
    ApiResponse<CourseReviewResponse> createReview(
            @PathVariable UUID courseId,
            @RequestBody CourseReviewRequest request
    ) {
        request.setCourseId(courseId);
        return ApiResponse.<CourseReviewResponse>builder()
                .result(courseReviewService.createReview(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<CourseReviewResponse>> getReviewsByCourse(@PathVariable UUID courseId) {
        return ApiResponse.<List<CourseReviewResponse>>builder()
                .result(courseReviewService.getReviewsByCourse(courseId))
                .build();
    }

    @PutMapping("/{reviewId}")
    ApiResponse<CourseReviewResponse> updateReview(
            @PathVariable UUID reviewId,
            @RequestBody CourseReviewRequest request
    ) {
        return ApiResponse.<CourseReviewResponse>builder()
                .result(courseReviewService.updateReview(reviewId, request))
                .build();
    }

    @DeleteMapping("/{reviewId}")
    ApiResponse<Void> deleteReview(@PathVariable UUID reviewId) {
        courseReviewService.deleteReview(reviewId);
        return ApiResponse.<Void>builder().build();
    }
}
