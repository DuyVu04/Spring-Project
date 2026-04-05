package com.example.spring_project.repository;

import com.example.spring_project.entity.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, UUID> {

    @Query("SELECT r FROM CourseReview r JOIN FETCH r.user JOIN FETCH r.course WHERE r.course.id = :courseId")
    List<CourseReview> findByCourseId(@Param("courseId") UUID courseId);

    boolean existsByUserIdAndCourseId(Long userId, UUID courseId);
}
