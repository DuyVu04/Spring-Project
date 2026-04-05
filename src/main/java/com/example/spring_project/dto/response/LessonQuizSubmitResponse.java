package com.example.spring_project.dto.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonQuizSubmitResponse {
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer percentageScore;
    private List<QuestionResult> results;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResult {
        private UUID questionId;
        private String questionText;
        private String selectedAnswer;
        private String correctAnswer;
        private Boolean correct;
        private String explanation;
    }
}