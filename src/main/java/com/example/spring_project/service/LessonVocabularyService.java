package com.example.spring_project.service;

import com.example.spring_project.dto.request.LessonVocabularyCreateRequest;
import com.example.spring_project.dto.response.LessonVocabularyResponse;
import com.example.spring_project.entity.Lesson;
import com.example.spring_project.entity.LessonVocabulary;
import com.example.spring_project.entity.Word;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.repository.LessonRepository;
import com.example.spring_project.repository.LessonVocabularyRepository;
import com.example.spring_project.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LessonVocabularyService {
    private final LessonVocabularyRepository lessonVocabularyRepository;
    private final LessonRepository lessonRepository;
    private final WordRepository wordRepository;

    @Transactional(readOnly = true)
    public List<LessonVocabularyResponse> getLessonVocabulary(UUID lessonId) {
        ensureLessonExists(lessonId);
        return lessonVocabularyRepository.findByLessonIdOrderByDisplayOrderAscCreatedAtAsc(lessonId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public LessonVocabularyResponse addLessonVocabulary(UUID lessonId, LessonVocabularyCreateRequest request) {
        if (request.getWordId() == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
        if (lessonVocabularyRepository.existsByLessonIdAndWordId(lessonId, request.getWordId())) {
            throw new CustomException(ErrorCode.RESOURCE_EXISTS);
        }

        Lesson lesson = ensureLessonExists(lessonId);
        Word word = wordRepository.findById(request.getWordId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        int displayOrder = request.getDisplayOrder() != null
                ? request.getDisplayOrder()
                : lessonVocabularyRepository.findTopByLessonIdOrderByDisplayOrderDesc(lessonId)
                .map(item -> item.getDisplayOrder() + 1)
                .orElse(1);

        LessonVocabulary lessonVocabulary = LessonVocabulary.builder()
                .lesson(lesson)
                .word(word)
                .displayOrder(displayOrder)
                .build();

        lessonVocabularyRepository.save(lessonVocabulary);
        return toResponse(lessonVocabulary);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteLessonVocabulary(UUID lessonId, UUID lessonVocabularyId) {
        ensureLessonExists(lessonId);
        LessonVocabulary lessonVocabulary = lessonVocabularyRepository.findById(lessonVocabularyId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!lessonVocabulary.getLesson().getId().equals(lessonId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        lessonVocabularyRepository.delete(lessonVocabulary);
    }

    private Lesson ensureLessonExists(UUID lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));
    }

    private LessonVocabularyResponse toResponse(LessonVocabulary lessonVocabulary) {
        Word word = lessonVocabulary.getWord();
        return LessonVocabularyResponse.builder()
                .id(lessonVocabulary.getId())
                .wordId(word.getId())
                .word(word.getWord())
                .pronunciation(word.getPronunciation())
                .definition(word.getDefinition())
                .definitionVi(word.getDefinitionVi())
                .exampleSentence(word.getExampleSentence())
                .exampleSentenceVi(word.getExampleSentenceVi())
                .level(word.getLevel())
                .displayOrder(lessonVocabulary.getDisplayOrder())
                .build();
    }
}