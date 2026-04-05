package com.example.spring_project.dto.response;

import java.time.Instant;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponse {
    private Long id;
    private String name;
    private String type;
    private List<Long> memberIds;
    private Long createdBy;
    private Instant createdAt;
}
