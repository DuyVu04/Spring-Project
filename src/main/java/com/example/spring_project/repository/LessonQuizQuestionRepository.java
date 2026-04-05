package com.example.spring_project.repository;

import com.example.spring_project.entity.LessonQuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LessonQuizQuestionRepository extends JpaRepository<LessonQuizQuestion, UUID> {
    List<LessonQuizQuestion> findByLessonIdOrderByDisplayOrderAscCreatedAtAsc(UUID lessonId);

    Optional<LessonQuizQuestion> findTopByLessonIdOrderByDisplayOrderDesc(UUID lessonId);
}