package com.example.spring_project.mapper;

import com.example.spring_project.dto.response.ChatMessageResponse;
import com.example.spring_project.dto.response.ChatMessageWSResponse;
import com.example.spring_project.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    @Mapping(source = "chatRoom.id", target = "roomId")
    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "sender.username", target = "senderName")
    @Mapping(source = "sender.avatarImg", target = "senderAvatar")
    @Mapping(source = "type", target = "messageType")
    ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage);

    @Mapping(source = "chatRoom.id", target = "roomId")
    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "sender.username", target = "senderName")
    @Mapping(source = "sender.avatarImg", target = "senderAvatar")
    @Mapping(source = "type", target = "messageType")
    ChatMessageWSResponse toChatMessageWSResponse(ChatMessage chatMessage);
}
