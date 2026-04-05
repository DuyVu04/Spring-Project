package com.example.spring_project.repository;

import com.example.spring_project.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    Optional<Enrollment> findByUserIdAndCourseId(Long userId, UUID courseId);

    List<Enrollment> findByUserId(Long userId);

    boolean existsByUserIdAndCourseId(Long userId, UUID courseId);
}
