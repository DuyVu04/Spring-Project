package com.example.spring_project.service;

import com.example.spring_project.dto.request.CourseReviewRequest;
import com.example.spring_project.dto.response.CourseReviewResponse;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.mapper.CourseReviewMapper;
import com.example.spring_project.repository.CourseRepository;
import com.example.spring_project.repository.CourseReviewRepository;
import com.example.spring_project.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseReviewService {

    private final CourseReviewRepository courseReviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseReviewMapper courseReviewMapper;
    private final CacheManager cacheManager;

    @CacheEvict(value = "course:reviews", key = "#request.courseId.toString()")
    public CourseReviewResponse createReview(CourseReviewRequest request) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        var course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        if (courseReviewRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
            throw new CustomException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        var review = courseReviewMapper.toCourseReview(request);
        review.setUser(user);
        review.setCourse(course);
        courseReviewRepository.save(review);
        updateCourseRating(course.getId());

        return courseReviewMapper.toCourseReviewResponse(review);
    }

    @Cacheable(value = "course:reviews", key = "#courseId.toString()", unless = "#result == null")
    public List<CourseReviewResponse> getReviewsByCourse(UUID courseId) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        return courseReviewRepository.findByCourseId(courseId).stream()
                .map(courseReviewMapper::toCourseReviewResponse)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "course:reviews", key = "#result.courseId.toString()")
    public CourseReviewResponse updateReview(UUID reviewId, CourseReviewRequest request) {
        var review = courseReviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        courseReviewRepository.save(review);

        updateCourseRating(review.getCourse().getId());

        return courseReviewMapper.toCourseReviewResponse(review);
    }

    public void deleteReview(UUID reviewId) {
        var review = courseReviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        UUID courseId = review.getCourse().getId();


        evictReviewCache(courseId);

        courseReviewRepository.delete(review);


        updateCourseRating(courseId);
    }

    private void evictReviewCache(UUID courseId) {
        var cache = cacheManager.getCache("course:reviews");
        if (cache != null) {
            cache.evict(courseId.toString());
        }
    }

    private void updateCourseRating(UUID courseId) {
        var reviews = courseReviewRepository.findByCourseId(courseId);
        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        if (reviews.isEmpty()) {
            course.setRating(0.0);
        } else {
            double avgRating = reviews.stream()
                    .mapToInt(r -> r.getRating())
                    .average()
                    .orElse(0.0);
            course.setRating(Math.round(avgRating * 10.0) / 10.0);
        }
        courseRepository.save(course);
    }
}
