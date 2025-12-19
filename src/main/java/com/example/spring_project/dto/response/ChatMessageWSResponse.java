package com.example.spring_project.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.Instant;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageWSResponse {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String content;
    private String messageType;
    private String senderName;
    private String senderAvatar;
    private String mediaUrl;
    private Instant createdAt;
}
