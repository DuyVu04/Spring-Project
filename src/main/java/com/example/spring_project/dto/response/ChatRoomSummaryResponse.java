package com.example.spring_project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomSummaryResponse {
    private Long roomId;
    private String roomName;
    private String type;
    private String avatar;
    private String lastMessage;
    private Long lastMessageSenderId;
    private Instant lastMessageTime;
    private List<Long> memberIds;
}
