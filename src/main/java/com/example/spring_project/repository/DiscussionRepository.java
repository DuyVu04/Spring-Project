package com.example.spring_project.repository;

import com.example.spring_project.entity.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiscussionRepository extends JpaRepository<Discussion, UUID> {

    List<Discussion> findByLessonIdAndParentDiscussionIsNull(UUID lessonId);

    List<Discussion> findByParentDiscussionId(UUID parentId);
}
