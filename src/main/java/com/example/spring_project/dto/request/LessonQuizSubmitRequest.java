package com.example.spring_project.dto.request;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonQuizSubmitRequest {
    private List<AnswerItem> answers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerItem {
        private UUID questionId;
        private String selectedAnswer;
    }
}