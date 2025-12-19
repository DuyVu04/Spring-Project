package com.example.spring_project.mapper;


import com.example.spring_project.dto.request.CreateRoomRequest;
import com.example.spring_project.entity.ChatRoom;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatRoomMapper {
    ChatRoom toChatRoomFromRequest(CreateRoomRequest chatRoom);


}
