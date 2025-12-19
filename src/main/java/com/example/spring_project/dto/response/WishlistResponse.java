package com.example.spring_project.dto.response;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WishlistResponse {

    private UUID id;

    private UUID courseId;

    private String courseTitle;

    private String courseThumbnail;

    private Instant createdAt;
}