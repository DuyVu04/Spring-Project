package com.example.spring_project.service;

import com.example.spring_project.dto.request.LessonResourceRequest;
import com.example.spring_project.dto.response.LessonResourceResponse;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.mapper.LessonResourceMapper;
import com.example.spring_project.repository.LessonRepository;
import com.example.spring_project.repository.LessonResourceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LessonResourceService {

    private final LessonResourceRepository lessonResourceRepository;
    private final LessonRepository lessonRepository;
    private final LessonResourceMapper lessonResourceMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public LessonResourceResponse addResource(LessonResourceRequest request) {
        var lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));

        var resource = lessonResourceMapper.toLessonResource(request);
        resource.setLesson(lesson);
        lessonResourceRepository.save(resource);

        return lessonResourceMapper.toLessonResourceResponse(resource);
    }

    public List<LessonResourceResponse> getResourcesByLesson(UUID lessonId) {
        lessonRepository.findById(lessonId)
                .orElseThrow(() -> new CustomException(ErrorCode.LESSON_NOT_FOUND));

        return lessonResourceRepository.findByLessonId(lessonId).stream()
                .map(lessonResourceMapper::toLessonResourceResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteResource(UUID resourceId) {
        if (!lessonResourceRepository.existsById(resourceId)) {
            throw new CustomException(ErrorCode.LESSON_NOT_FOUND);
        }
        lessonResourceRepository.deleteById(resourceId);
    }
}
