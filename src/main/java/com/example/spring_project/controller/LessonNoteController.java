package com.example.spring_project.controller;

import com.example.spring_project.dto.request.LessonNoteRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.LessonNoteResponse;
import com.example.spring_project.service.LessonNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lessons/{lessonId}/notes")
@RequiredArgsConstructor
public class LessonNoteController {

    private final LessonNoteService lessonNoteService;

    @PostMapping
    ApiResponse<LessonNoteResponse> createNote(
            @PathVariable UUID lessonId,
            @RequestBody LessonNoteRequest request
    ) {
        request.setLessonId(lessonId);
        return ApiResponse.<LessonNoteResponse>builder()
                .result(lessonNoteService.createNote(request))
                .build();
    }

    @GetMapping("/my")
    ApiResponse<List<LessonNoteResponse>> getMyNotesByLesson(@PathVariable UUID lessonId) {
        return ApiResponse.<List<LessonNoteResponse>>builder()
                .result(lessonNoteService.getMyNotesByLesson(lessonId))
                .build();
    }

    @PutMapping("/{noteId}")
    ApiResponse<LessonNoteResponse> updateNote(
            @PathVariable UUID noteId,
            @RequestBody LessonNoteRequest request
    ) {
        return ApiResponse.<LessonNoteResponse>builder()
                .result(lessonNoteService.updateNote(noteId, request))
                .build();
    }

    @DeleteMapping("/{noteId}")
    ApiResponse<Void> deleteNote(@PathVariable UUID noteId) {
        lessonNoteService.deleteNote(noteId);
        return ApiResponse.<Void>builder().build();
    }
}
