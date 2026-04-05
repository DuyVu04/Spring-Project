package com.example.spring_project.repository;

import com.example.spring_project.entity.LessonResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonResourceRepository extends JpaRepository<LessonResource, UUID> {

    List<LessonResource> findByLessonId(UUID lessonId);
}
