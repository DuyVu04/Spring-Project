package com.example.spring_project.mapper;

import com.example.spring_project.dto.response.FriendshipResponse;
import com.example.spring_project.entity.Friendship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendshipMapper {

    @Mapping(target = "requester.roles", ignore = true)
    @Mapping(target = "addressee.roles", ignore = true)
    FriendshipResponse toFriendshipResponse(Friendship friendship);
}
