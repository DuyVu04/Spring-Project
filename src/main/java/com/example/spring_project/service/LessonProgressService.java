package com.example.spring_project.service;

import com.example.spring_project.dto.request.LessonProgressRequest;
import com.example.spring_project.dto.response.LessonProgressResponse;
import com.example.spring_project.entity.LessonProgress;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.mapper.LessonProgressMapper;
import com.example.spring_project.repository.EnrollmentRepository;
import com.example.spring_project.repository.LessonProgressRepository;
import com.example.spring_project.repository.LessonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LessonProgressService {

    private final LessonProgressRepository lessonProgressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressMapper lessonProgressMapper;

    public LessonProgressResponse updateProgress(LessonProgressRequest request) {
        var enrollment = enrollmentRepository.findById(request.getEnrollmentId())
                .orElseThrow(() -> new CustomException(ErrorCode.ENROLLMENT_NOT_FOUND));

        var lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));

        var progress = lessonProgressRepository
                .findByEnrollmentIdAndLessonId(enrollment.getId(), lesson.getId())
                .orElse(LessonProgress.builder()
                        .enrollment(enrollment)
                        .lesson(lesson)
                        .watchedDuration(0)
                        .completed(false)
                        .build());

        progress.setWatchedDuration(request.getWatchedDuration());
        if (request.getCompleted() != null) {
            progress.setCompleted(request.getCompleted());
        }
        progress.setLastWatchedAt(LocalDateTime.now());

        lessonProgressRepository.save(progress);

        return lessonProgressMapper.toLessonProgressResponse(progress);
    }

    public List<LessonProgressResponse> getProgressByEnrollment(UUID enrollmentId) {
        enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENROLLMENT_NOT_FOUND));

        return lessonProgressRepository.findByEnrollmentId(enrollmentId).stream()
                .map(lessonProgressMapper::toLessonProgressResponse)
                .collect(Collectors.toList());
    }
}
