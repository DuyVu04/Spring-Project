package com.example.spring_project.controller;

import com.example.spring_project.dto.request.EnrollmentRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.EnrollmentResponse;
import com.example.spring_project.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    ApiResponse<EnrollmentResponse> enrollCourse(@RequestBody EnrollmentRequest request) {
        return ApiResponse.<EnrollmentResponse>builder()
                .result(enrollmentService.enrollCourse(request))
                .build();
    }

    @GetMapping("/my")
    ApiResponse<List<EnrollmentResponse>> getMyEnrollments() {
        return ApiResponse.<List<EnrollmentResponse>>builder()
                .result(enrollmentService.getMyEnrollments())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<EnrollmentResponse> getEnrollmentById(@PathVariable UUID id) {
        return ApiResponse.<EnrollmentResponse>builder()
                .result(enrollmentService.getEnrollmentById(id))
                .build();
    }
}
