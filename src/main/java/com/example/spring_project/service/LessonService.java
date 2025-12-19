package com.example.spring_project.service;

import com.example.spring_project.dto.request.LessonRequest;
import com.example.spring_project.dto.response.LessonResponse;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.mapper.LessonMapper;
import com.example.spring_project.repository.CourseRepository;
import com.example.spring_project.repository.LessonRepository;
import com.example.spring_project.repository.ModuleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final LessonMapper lessonMapper;
    private final CacheManager cacheManager;

    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "lessons:byModule", key = "#moduleId.toString()")
    public LessonResponse createLesson(UUID moduleId, LessonRequest request) {
        var module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.MODULE_NOT_FOUND));

        var lesson = lessonMapper.toLesson(request);
        lesson.setModule(module);
        lessonRepository.save(lesson);

        var course = module.getCourse();
        if (course != null) {
            course.setTotalLessons(course.getTotalLessons() + 1);
            if (request.getDuration() != null) {
                course.setTotalDuration(course.getTotalDuration() + request.getDuration());
            }
            courseRepository.save(course);
        }

        return lessonMapper.toLessonResponse(lesson);
    }

    @Cacheable(value = "lessons:byModule", key = "#moduleId.toString()", unless = "#result == null")
    public List<LessonResponse> getLessonsByModule(UUID moduleId) {
        moduleRepository.findById(moduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.MODULE_NOT_FOUND));

        return lessonRepository.findByModuleIdOrderByOrderIndexAsc(moduleId).stream()
                .map(lessonMapper::toLessonResponse)
                .collect(Collectors.toList());
    }

    public LessonResponse getLessonById(UUID lessonId) {
        var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));
        return lessonMapper.toLessonResponse(lesson);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public LessonResponse updateLesson(UUID lessonId, LessonRequest request) {
        var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));


        if (lesson.getModule() != null) {
            evictLessonCache(lesson.getModule().getId());
        }


        var course = lesson.getModule() != null ? lesson.getModule().getCourse() : null;
        if (course != null && request.getDuration() != null) {
            long oldDuration = lesson.getDuration() != null ? lesson.getDuration() : 0;
            course.setTotalDuration(course.getTotalDuration() - oldDuration + request.getDuration());
            courseRepository.save(course);
        }

        lesson.setTitle(request.getTitle());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setDuration(request.getDuration());
        lesson.setOrderIndex(request.getOrderIndex() != null ? request.getOrderIndex().longValue() : lesson.getOrderIndex());
        lesson.setPreview(request.getPreview() != null ? request.getPreview() : lesson.isPreview());
        lesson.setLocked(request.getLocked() != null ? request.getLocked() : lesson.isLocked());
        lessonRepository.save(lesson);

        return lessonMapper.toLessonResponse(lesson);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteLesson(UUID lessonId) {
        var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));


        if (lesson.getModule() != null) {
            evictLessonCache(lesson.getModule().getId());
        }

        var course = lesson.getModule() != null ? lesson.getModule().getCourse() : null;
        if (course != null) {
            course.setTotalLessons(Math.max(0, course.getTotalLessons() - 1));
            long duration = lesson.getDuration() != null ? lesson.getDuration() : 0;
            course.setTotalDuration(Math.max(0, course.getTotalDuration() - duration));
            courseRepository.save(course);
        }

        lessonRepository.delete(lesson);
    }

    private void evictLessonCache(UUID moduleId) {
        var cache = cacheManager.getCache("lessons:byModule");
        if (cache != null) {
            cache.evict(moduleId.toString());
        }
    }
}
