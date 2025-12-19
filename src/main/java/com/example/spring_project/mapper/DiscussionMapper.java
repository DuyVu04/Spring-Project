package com.example.spring_project.mapper;

import com.example.spring_project.dto.request.DiscussionRequest;
import com.example.spring_project.dto.response.DiscussionResponse;
import com.example.spring_project.entity.Discussion;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DiscussionMapper {

    Discussion toDiscussion(DiscussionRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "lesson.id", target = "lessonId")
    @Mapping(source = "parentDiscussion.id", target = "parentDiscussionId")
    DiscussionResponse toDiscussionResponse(Discussion discussion);
}