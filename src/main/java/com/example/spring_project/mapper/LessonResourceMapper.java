package com.example.spring_project.mapper;

import com.example.spring_project.dto.request.LessonResourceRequest;
import com.example.spring_project.dto.response.LessonResourceResponse;
import com.example.spring_project.entity.LessonResource;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LessonResourceMapper {

    LessonResource toLessonResource(LessonResourceRequest request);

    LessonResourceResponse toLessonResourceResponse(LessonResource resource);
}