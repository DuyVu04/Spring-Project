package com.example.spring_project.mapper;

import com.example.spring_project.dto.request.LessonRequest;
import com.example.spring_project.dto.response.LessonResponse;
import com.example.spring_project.entity.Lesson;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LessonMapper {

    Lesson toLesson(LessonRequest request);

    LessonResponse toLessonResponse(Lesson lesson);
}