//package com.example.spring_project.service;
//
//import com.example.spring_project.dto.request.EnrollmentRequest;
//import com.example.spring_project.dto.response.EnrollmentResponse;
//import com.example.spring_project.entity.Enrollment;
//import com.example.spring_project.enums.EnrollmentStatus;
//import com.example.spring_project.exception.CustomException;
//import com.example.spring_project.exception.ErrorCode;
//import com.example.spring_project.mapper.EnrollmentMapper;
//import com.example.spring_project.repository.CourseRepository;
//import com.example.spring_project.repository.EnrollmentRepository;
//import com.example.spring_project.repository.UserRepository;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//@Transactional
//public class EnrollmentService {
//
//    private final EnrollmentRepository enrollmentRepository;
//    private final CourseRepository courseRepository;
//    private final UserRepository userRepository;
//    private final EnrollmentMapper enrollmentMapper;
//
//    public EnrollmentResponse enrollCourse(EnrollmentRequest request) {
//        var username = SecurityContextHolder.getContext().getAuthentication().getName();
//        var user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));
//
//        var course = courseRepository.findById(request.getCourseId())
//                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));
//
//        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
//            throw new CustomException(ErrorCode.ENROLLMENT_ALREADY_EXISTS);
//        }
//
//        var enrollment = Enrollment.builder()
//                .user(user)
//                .course(course)
//                .completionPercent(0)
//                .status(EnrollmentStatus.IN_PROGRESS)
//                .build();
//
//        enrollmentRepository.save(enrollment);
//
//        // Cập nhật totalEnrolled
//        course.setTotalEnrolled(course.getTotalEnrolled() + 1);
//        courseRepository.save(course);
//        var enrollmentResponse = enrollmentMapper.toEnrollmentResponse(enrollment);
//        enrollmentResponse.setCourseThumbnail(course.getThumbnail());
//        return enrollmentResponse;
//    }
//
//    public List<EnrollmentResponse> getMyEnrollments() {
//        var username = SecurityContextHolder.getContext().getAuthentication().getName();
//        var user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));
//
//        return enrollmentRepository.findByUserId(user.getId()).stream()
//                .map(enrollment -> {
//                    var enrollmentResponse = enrollmentMapper.toEnrollmentResponse(enrollment);
//                    // Set course thumbnail for each enrollment
//                    var course = enrollment.getCourse();
//                    if (course != null) {
//                        enrollmentResponse.setCourseThumbnail(course.getThumbnail());
//                        enrollmentResponse.setInstructorName(course.getInstructorName());
//                        enrollmentResponse.setTotalLessons(course.getTotalLessons());
//                    }
//                    return enrollmentResponse;
//                })
//                .collect(Collectors.toList());
//    }
//
//    public EnrollmentResponse getEnrollmentById(UUID id) {
//        var enrollment = enrollmentRepository.findById(id)
//                .orElseThrow(() -> new CustomException(ErrorCode.ENROLLMENT_NOT_FOUND));
//        return enrollmentMapper.toEnrollmentResponse(enrollment);
//    }
//}

package com.example.spring_project.service;

import com.example.spring_project.dto.request.EnrollmentRequest;
import com.example.spring_project.dto.response.EnrollmentResponse;
import com.example.spring_project.entity.Enrollment;
import com.example.spring_project.enums.EnrollmentStatus;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.mapper.EnrollmentMapper;
import com.example.spring_project.repository.CourseRepository;
import com.example.spring_project.repository.EnrollmentRepository;
import com.example.spring_project.repository.UserRepository;
import com.example.spring_project.service.MinioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final MinioService minioService;

    @CacheEvict(value = "enrollments:my", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName() + ':courses'")
    public EnrollmentResponse enrollCourse(EnrollmentRequest request) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        var course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND));

        if (!course.isFree() && user.getMembershipType() != com.example.spring_project.enums.MembershipType.PREMIUM) {
            throw new CustomException(ErrorCode.PREMIUM_REQUIRED);
        }

        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
            throw new CustomException(ErrorCode.ENROLLMENT_ALREADY_EXISTS);
        }

        var enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .completionPercent(0)
                .status(EnrollmentStatus.IN_PROGRESS)
                .build();

        enrollmentRepository.save(enrollment);


        course.setTotalEnrolled(course.getTotalEnrolled() + 1);
        courseRepository.save(course);
        var enrollmentResponse = enrollmentMapper.toEnrollmentResponse(enrollment);
        try {
            if (course.getThumbnail() != null && !course.getThumbnail().isEmpty()) {
                enrollmentResponse.setCourseThumbnail(minioService.presignedUrl(course.getThumbnail()));
            }
        } catch (Exception e) {
            log.error("Failed to generate presigned URL", e);
            enrollmentResponse.setCourseThumbnail(course.getThumbnail());
        }
        return enrollmentResponse;
    }

    @Cacheable(value = "enrollments:my", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName() + ':courses'", unless = "#result == null")
    public List<EnrollmentResponse> getMyEnrollments() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        return enrollmentRepository.findByUserId(user.getId()).stream()
                .map(enrollment -> {
                    var enrollmentResponse = enrollmentMapper.toEnrollmentResponse(enrollment);

                    var course = enrollment.getCourse();
                    if (course != null) {
                        try {
                            if (course.getThumbnail() != null && !course.getThumbnail().isEmpty()) {
                                enrollmentResponse.setCourseThumbnail(minioService.presignedUrl(course.getThumbnail()));
                            } else {
                                enrollmentResponse.setCourseThumbnail(null);
                            }
                        } catch (Exception e) {
                            log.error("Failed to generate presigned URL for course: " + course.getId(), e);
                            enrollmentResponse.setCourseThumbnail(course.getThumbnail());
                        }
                        enrollmentResponse.setInstructorName(course.getInstructorName());
                        enrollmentResponse.setTotalLessons(course.getTotalLessons());
                    }
                    return enrollmentResponse;
                })
                .collect(Collectors.toList());
    }

    public EnrollmentResponse getEnrollmentById(UUID id) {
        var enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ENROLLMENT_NOT_FOUND));
        var enrollmentResponse = enrollmentMapper.toEnrollmentResponse(enrollment);
        var course = enrollment.getCourse();
        if (course != null) {
            try {
                if (course.getThumbnail() != null && !course.getThumbnail().isEmpty()) {
                    enrollmentResponse.setCourseThumbnail(minioService.presignedUrl(course.getThumbnail()));
                }
            } catch (Exception e) {
                log.error("Failed to generate presigned URL", e);
                enrollmentResponse.setCourseThumbnail(course.getThumbnail());
            }
            enrollmentResponse.setInstructorName(course.getInstructorName());
            enrollmentResponse.setTotalLessons(course.getTotalLessons());
        }
        return enrollmentResponse;
    }
}

