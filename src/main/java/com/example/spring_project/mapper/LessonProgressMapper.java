package com.example.spring_project.mapper;

import com.example.spring_project.dto.response.LessonProgressResponse;
import com.example.spring_project.entity.LessonProgress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LessonProgressMapper {

    @Mapping(source = "lesson.id", target = "lessonId")
    @Mapping(source = "lesson.title", target = "lessonTitle")
    LessonProgressResponse toLessonProgressResponse(LessonProgress progress);
}