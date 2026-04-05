package com.example.spring_project.repository;

import com.example.spring_project.entity.LessonVocabulary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LessonVocabularyRepository extends JpaRepository<LessonVocabulary, UUID> {
    List<LessonVocabulary> findByLessonIdOrderByDisplayOrderAscCreatedAtAsc(UUID lessonId);

    boolean existsByLessonIdAndWordId(UUID lessonId, UUID wordId);

    Optional<LessonVocabulary> findTopByLessonIdOrderByDisplayOrderDesc(UUID lessonId);
}