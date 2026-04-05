package com.example.spring_project.repository;

import com.example.spring_project.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModuleRepository extends JpaRepository<Module, UUID> {

    @Query("SELECT DISTINCT m FROM Module m LEFT JOIN FETCH m.lessons WHERE m.course.id = :courseId ORDER BY m.orderIndex ASC")
    List<Module> findByCourseIdOrderByOrderIndexAsc(@Param("courseId") UUID courseId);
}
