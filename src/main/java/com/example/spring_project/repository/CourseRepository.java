package com.example.spring_project.repository;

import com.example.spring_project.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Category, String> {
}
