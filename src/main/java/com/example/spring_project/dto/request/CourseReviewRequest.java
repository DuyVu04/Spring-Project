package com.example.spring_project.dto.request;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseReviewRequest {

    private UUID courseId;

    private Integer rating;

    private String comment;
}