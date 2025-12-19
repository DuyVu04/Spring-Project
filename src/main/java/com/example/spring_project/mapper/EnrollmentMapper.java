package com.example.spring_project.mapper;

import com.example.spring_project.dto.response.EnrollmentResponse;
import com.example.spring_project.entity.Enrollment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "course.title", target = "courseTitle")
    EnrollmentResponse toEnrollmentResponse(Enrollment enrollment);
}