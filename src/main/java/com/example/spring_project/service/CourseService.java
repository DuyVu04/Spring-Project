package com.example.spring_project.service;

import com.example.spring_project.dto.request.CourseRequest;
import com.example.spring_project.dto.response.CourseResponse;
import com.example.spring_project.dto.response.PageResponse;
import com.example.spring_project.entity.Course;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.mapper.CourseMapper;
import com.example.spring_project.mapper.PageResponseMapper;
import com.example.spring_project.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final PageResponseMapper pageResponseMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "courses:all", allEntries = true)
    public CourseResponse createCourse(CourseRequest request) {
        var course = courseMapper.toCourse(request);
        course.setTotalDuration(0L);
        course.setTotalModules(0L);
        course.setTotalLessons(0L);
        course.setRating(0.0);
        course.setTotalEnrolled(0L);
        courseRepository.save(course);
        return courseMapper.toCourseResponse(course);
    }

    @Cacheable(value = "courses", key = "#id.toString()", unless = "#result == null")
    public CourseResponse getCourseById(UUID id) {
        log.info("Fetching course from DB: {}", id);
        var course = courseRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));
        return courseMapper.toCourseResponse(course);
    }

    @Cacheable(value = "courses:all", key = "'list'", unless = "#result == null")
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(courseMapper::toCourseResponse)
                .collect(Collectors.toList());
    }

    public PageResponse<CourseResponse> getCoursesPaged(Specification<Course> spec, Pageable pageable) {
        var coursePage = courseRepository.findAll(spec, pageable)
                .map(courseMapper::toCourseResponse);
        return pageResponseMapper.toPageResponse(coursePage);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Caching(evict = {
            @CacheEvict(value = "courses", key = "#id.toString()"),
            @CacheEvict(value = "courses:all", allEntries = true)
    })
    public CourseResponse updateCourse(UUID id, CourseRequest request) {
        var course = courseRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));
        courseMapper.updateCourseFromRequest(course, request);
        courseRepository.save(course);
        return courseMapper.toCourseResponse(course);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Caching(evict = {
            @CacheEvict(value = "courses", key = "#id.toString()"),
            @CacheEvict(value = "courses:all", allEntries = true)
    })
    public void deleteCourse(UUID id) {
        var course = courseRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));
        courseRepository.delete(course);
    }
}
