package com.example.spring_project.mapper;

import com.example.spring_project.dto.request.CourseReviewRequest;
import com.example.spring_project.dto.response.CourseReviewResponse;
import com.example.spring_project.entity.CourseReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseReviewMapper {

    CourseReview toCourseReview(CourseReviewRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "course.id", target = "courseId")
    CourseReviewResponse toCourseReviewResponse(CourseReview review);
}