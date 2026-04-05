package com.example.spring_project.service;

import com.example.spring_project.dto.request.LessonQuizQuestionRequest;
import com.example.spring_project.dto.request.LessonQuizSubmitRequest;
import com.example.spring_project.dto.response.LessonQuizQuestionResponse;
import com.example.spring_project.dto.response.LessonQuizSubmitResponse;
import com.example.spring_project.dto.response.AdminLessonQuizQuestionResponse;
import com.example.spring_project.entity.Lesson;
import com.example.spring_project.entity.LessonQuizQuestion;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.repository.LessonQuizQuestionRepository;
import com.example.spring_project.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class LessonQuizService {
    private final LessonQuizQuestionRepository lessonQuizQuestionRepository;
    private final LessonRepository lessonRepository;

    @Transactional(readOnly = true)
    public List<LessonQuizQuestionResponse> getQuizQuestions(UUID lessonId) {
        ensureLessonExists(lessonId);
        return lessonQuizQuestionRepository.findByLessonIdOrderByDisplayOrderAscCreatedAtAsc(lessonId)
                .stream()
                .map(this::toQuestionResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public LessonQuizQuestionResponse createQuestion(UUID lessonId, LessonQuizQuestionRequest request) {
        validateQuestionRequest(request);
        Lesson lesson = ensureLessonExists(lessonId);

        int displayOrder = request.getDisplayOrder() != null
                ? request.getDisplayOrder()
                : lessonQuizQuestionRepository.findTopByLessonIdOrderByDisplayOrderDesc(lessonId)
                .map(item -> item.getDisplayOrder() + 1)
                .orElse(1);

        LessonQuizQuestion entity = LessonQuizQuestion.builder()
                .lesson(lesson)
                .questionText(request.getQuestionText().trim())
                .optionA(request.getOptionA().trim())
                .optionB(request.getOptionB().trim())
                .optionC(request.getOptionC().trim())
                .optionD(request.getOptionD().trim())
                .correctAnswer(normalizeAnswer(request.getCorrectAnswer()))
                .explanation(blankToNull(request.getExplanation()))
                .displayOrder(displayOrder)
                .build();

        lessonQuizQuestionRepository.save(entity);
        return toQuestionResponse(entity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public LessonQuizQuestionResponse updateQuestion(UUID lessonId, UUID questionId, LessonQuizQuestionRequest request) {
        validateQuestionRequest(request);
        ensureLessonExists(lessonId);

        LessonQuizQuestion entity = lessonQuizQuestionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!entity.getLesson().getId().equals(lessonId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        entity.setQuestionText(request.getQuestionText().trim());
        entity.setOptionA(request.getOptionA().trim());
        entity.setOptionB(request.getOptionB().trim());
        entity.setOptionC(request.getOptionC().trim());
        entity.setOptionD(request.getOptionD().trim());
        entity.setCorrectAnswer(normalizeAnswer(request.getCorrectAnswer()));
        entity.setExplanation(blankToNull(request.getExplanation()));
        entity.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : entity.getDisplayOrder());

        lessonQuizQuestionRepository.save(entity);
        return toQuestionResponse(entity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteQuestion(UUID lessonId, UUID questionId) {
        ensureLessonExists(lessonId);
        LessonQuizQuestion entity = lessonQuizQuestionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!entity.getLesson().getId().equals(lessonId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        lessonQuizQuestionRepository.delete(entity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public AdminLessonQuizQuestionResponse getQuestionDetail(UUID lessonId, UUID questionId) {
        ensureLessonExists(lessonId);
        LessonQuizQuestion entity = lessonQuizQuestionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!entity.getLesson().getId().equals(lessonId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        return AdminLessonQuizQuestionResponse.builder()
                .id(entity.getId())
                .questionText(entity.getQuestionText())
                .optionA(entity.getOptionA())
                .optionB(entity.getOptionB())
                .optionC(entity.getOptionC())
                .optionD(entity.getOptionD())
                .correctAnswer(entity.getCorrectAnswer())
                .displayOrder(entity.getDisplayOrder())
                .explanation(entity.getExplanation())
                .build();
    }

    @Transactional(readOnly = true)
    public LessonQuizSubmitResponse submitQuiz(UUID lessonId, LessonQuizSubmitRequest request) {
        ensureLessonExists(lessonId);
        List<LessonQuizQuestion> questions = lessonQuizQuestionRepository.findByLessonIdOrderByDisplayOrderAscCreatedAtAsc(lessonId);

        Map<UUID, String> answersByQuestionId = new LinkedHashMap<>();
        if (request != null && request.getAnswers() != null) {
            for (LessonQuizSubmitRequest.AnswerItem answer : request.getAnswers()) {
                if (answer == null || answer.getQuestionId() == null) {
                    continue;
                }
                answersByQuestionId.put(answer.getQuestionId(), normalizeAnswerNullable(answer.getSelectedAnswer()));
            }
        }

        int correctCount = 0;
        List<LessonQuizSubmitResponse.QuestionResult> results = new ArrayList<>();

        for (LessonQuizQuestion question : questions) {
            String selectedAnswer = answersByQuestionId.get(question.getId());
            boolean correct = question.getCorrectAnswer().equals(selectedAnswer);
            if (correct) {
                correctCount++;
            }

            results.add(LessonQuizSubmitResponse.QuestionResult.builder()
                    .questionId(question.getId())
                    .questionText(question.getQuestionText())
                    .selectedAnswer(selectedAnswer)
                    .correctAnswer(question.getCorrectAnswer())
                    .correct(correct)
                    .explanation(question.getExplanation())
                    .build());
        }

        int totalQuestions = questions.size();
        int percentage = totalQuestions == 0 ? 0 : (int) Math.round((correctCount * 100.0) / totalQuestions);

        return LessonQuizSubmitResponse.builder()
                .totalQuestions(totalQuestions)
                .correctAnswers(correctCount)
                .percentageScore(percentage)
                .results(results)
                .build();
    }

    private Lesson ensureLessonExists(UUID lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));
    }

    private void validateQuestionRequest(LessonQuizQuestionRequest request) {
        if (request == null
                || isBlank(request.getQuestionText())
                || isBlank(request.getOptionA())
                || isBlank(request.getOptionB())
                || isBlank(request.getOptionC())
                || isBlank(request.getOptionD())
                || isBlank(request.getCorrectAnswer())) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        String answer = normalizeAnswer(request.getCorrectAnswer());
        if (!Set.of("A", "B", "C", "D").contains(answer)) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
    }

    private LessonQuizQuestionResponse toQuestionResponse(LessonQuizQuestion entity) {
        return LessonQuizQuestionResponse.builder()
                .id(entity.getId())
                .questionText(entity.getQuestionText())
                .optionA(entity.getOptionA())
                .optionB(entity.getOptionB())
                .optionC(entity.getOptionC())
                .optionD(entity.getOptionD())
                .displayOrder(entity.getDisplayOrder())
                .explanation(entity.getExplanation())
                .build();
    }

    private String normalizeAnswer(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeAnswerNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}