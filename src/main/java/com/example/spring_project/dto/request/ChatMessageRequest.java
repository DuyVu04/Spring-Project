package com.example.spring_project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest {
    private Long roomId;
    private String content;
    private String messageType; // TEXT, IMAGE, AUDIO
    private String mediaUrl;
}
