package com.example.spring_project.dto.request;

import com.example.spring_project.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseRequest {
    private String title;
    private String description;
    private String thumbnail;
    private String instructorName;
    private String instructorAvatar;
    private String instructorTitle;
    private boolean free;
    private CourseStatus status;


}
