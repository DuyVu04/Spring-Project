package com.example.spring_project.repository;

import com.example.spring_project.entity.Course;
import com.example.spring_project.enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID>, JpaSpecificationExecutor<Course> {

    List<Course> findByStatus(CourseStatus status);

    Page<Course> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
