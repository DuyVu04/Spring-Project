package com.example.spring_project.dto.response;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscussionResponse {

    private UUID id;

    private String content;

    private Long userId;

    private String username;

    private UUID lessonId;

    private UUID parentDiscussionId;

    private List<DiscussionResponse> replies;

    private Instant createdAt;
}