package com.example.spring_project.controller;

import com.example.spring_project.dto.request.LessonQuizQuestionRequest;
import com.example.spring_project.dto.request.LessonQuizSubmitRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.AdminLessonQuizQuestionResponse;
import com.example.spring_project.dto.response.LessonQuizQuestionResponse;
import com.example.spring_project.dto.response.LessonQuizSubmitResponse;
import com.example.spring_project.service.LessonQuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lessons/{lessonId}/quiz")
@RequiredArgsConstructor
public class LessonQuizController {
    private final LessonQuizService lessonQuizService;

    @GetMapping
    ApiResponse<List<LessonQuizQuestionResponse>> getQuiz(@PathVariable UUID lessonId) {
        return ApiResponse.<List<LessonQuizQuestionResponse>>builder()
                .result(lessonQuizService.getQuizQuestions(lessonId))
                .build();
    }

    @GetMapping("/questions/{questionId}")
    ApiResponse<AdminLessonQuizQuestionResponse> getQuestionDetail(
            @PathVariable UUID lessonId,
            @PathVariable UUID questionId
    ) {
        return ApiResponse.<AdminLessonQuizQuestionResponse>builder()
                .result(lessonQuizService.getQuestionDetail(lessonId, questionId))
                .build();
    }

    @PostMapping("/submit")
    ApiResponse<LessonQuizSubmitResponse> submitQuiz(
            @PathVariable UUID lessonId,
            @RequestBody(required = false) LessonQuizSubmitRequest request
    ) {
        return ApiResponse.<LessonQuizSubmitResponse>builder()
                .result(lessonQuizService.submitQuiz(lessonId, request))
                .build();
    }

    @PostMapping("/questions")
    ApiResponse<LessonQuizQuestionResponse> createQuestion(
            @PathVariable UUID lessonId,
            @RequestBody LessonQuizQuestionRequest request
    ) {
        return ApiResponse.<LessonQuizQuestionResponse>builder()
                .result(lessonQuizService.createQuestion(lessonId, request))
                .build();
    }

    @PutMapping("/questions/{questionId}")
    ApiResponse<LessonQuizQuestionResponse> updateQuestion(
            @PathVariable UUID lessonId,
            @PathVariable UUID questionId,
            @RequestBody LessonQuizQuestionRequest request
    ) {
        return ApiResponse.<LessonQuizQuestionResponse>builder()
                .result(lessonQuizService.updateQuestion(lessonId, questionId, request))
                .build();
    }

    @DeleteMapping("/questions/{questionId}")
    ApiResponse<Void> deleteQuestion(
            @PathVariable UUID lessonId,
            @PathVariable UUID questionId
    ) {
        lessonQuizService.deleteQuestion(lessonId, questionId);
        return ApiResponse.<Void>builder().build();
    }
}