package com.example.spring_project.mapper;

import com.example.spring_project.dto.request.CourseRequest;
import com.example.spring_project.dto.response.CourseResponse;
import com.example.spring_project.entity.Course;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    Course toCourse(CourseRequest request);

    CourseResponse toCourseResponse(Course course);

    void updateCourseFromRequest(@MappingTarget Course course, CourseRequest request);
}