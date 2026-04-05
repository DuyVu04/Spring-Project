package com.example.spring_project.controller;

import com.example.spring_project.dto.request.CourseRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.CourseResponse;
import com.example.spring_project.dto.response.PageResponse;
import com.example.spring_project.entity.Course;
import com.example.spring_project.service.CourseService;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    ApiResponse<CourseResponse> createCourse(@RequestBody CourseRequest courseRequest) {
        return ApiResponse.<CourseResponse>builder()
                .result(courseService.createCourse(courseRequest))
                .build();
    }

    @GetMapping
    ApiResponse<PageResponse<CourseResponse>> getAll(
            @Filter Specification<Course> spec, Pageable page){
        return ApiResponse.<PageResponse<CourseResponse>>builder()
                .message("Get all course successfully")
                .result(courseService.getCoursesPaged(spec,page))
                .build();
    }

    @GetMapping("/{courseId}")
    ApiResponse<CourseResponse> getCourse(@PathVariable UUID courseId){
        return ApiResponse.<CourseResponse>builder()
                .result( courseService.getCourseById(courseId))
                .build();
    }

    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<CourseResponse> updateCourse(
            @PathVariable UUID courseId,
            @RequestBody CourseRequest courseRequest
    ) {
        return ApiResponse.<CourseResponse>builder()
                .result(courseService.updateCourse(courseId, courseRequest))
                .build();
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<Void> deleteCourse(@PathVariable UUID courseId) {
        courseService.deleteCourse(courseId);
        return ApiResponse.<Void>builder().build();
    }

}
