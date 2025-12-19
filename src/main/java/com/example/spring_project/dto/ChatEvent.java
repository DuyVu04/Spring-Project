package com.example.spring_project.dto;

import com.example.spring_project.enums.ChatEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatEvent<T> {
    private ChatEventType type;
    private T payload;
}
