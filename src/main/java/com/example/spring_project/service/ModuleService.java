package com.example.spring_project.service;

import com.example.spring_project.dto.request.ModuleRequest;
import com.example.spring_project.dto.response.ModuleResponse;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.mapper.ModuleMapper;
import com.example.spring_project.repository.CourseRepository;
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
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final ModuleMapper moduleMapper;
    private final CacheManager cacheManager;

    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "modules:byCourse", key = "#courseId.toString()")
    public ModuleResponse createModule(UUID courseId, ModuleRequest request) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        var module = moduleMapper.toModule(request);
        module.setCourse(course);
        moduleRepository.save(module);

        course.setTotalModules(course.getTotalModules() + 1);
        courseRepository.save(course);

        return moduleMapper.toModuleResponse(module);
    }

    @Cacheable(value = "modules:byCourse", key = "#courseId.toString()", unless = "#result == null")
    public List<ModuleResponse> getModulesByCourse(UUID courseId) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        return moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId).stream()
                .map(moduleMapper::toModuleResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ModuleResponse updateModule(UUID moduleId, ModuleRequest request) {
        var module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.MODULE_NOT_FOUND));

        evictModuleCache(module.getCourse().getId());

        module.setTitle(request.getTitle());
        module.setOrderIndex(request.getOrderIndex());
        moduleRepository.save(module);

        return moduleMapper.toModuleResponse(module);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteModule(UUID moduleId) {
        var module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.MODULE_NOT_FOUND));

        var course = module.getCourse();

        if (course != null) {
            evictModuleCache(course.getId());
            course.setTotalModules(Math.max(0, course.getTotalModules() - 1));
            long lessonCount = module.getLessons() != null ? module.getLessons().size() : 0;
            course.setTotalLessons(Math.max(0, course.getTotalLessons() - lessonCount));
            courseRepository.save(course);
        }

        moduleRepository.delete(module);
    }

    private void evictModuleCache(UUID courseId) {
        var cache = cacheManager.getCache("modules:byCourse");
        if (cache != null) {
            cache.evict(courseId.toString());
        }
    }
}
