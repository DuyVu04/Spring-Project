package com.example.spring_project.mapper;
import com.example.spring_project.dto.request.UserRequest;
import com.example.spring_project.dto.request.UserUpdateRequest;
import com.example.spring_project.dto.response.UserResponse;
import com.example.spring_project.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserRequest userRequest);
    List<UserResponse> toListUserResponse(List<User> user);
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUserFromRequest(@MappingTarget User user , UserUpdateRequest userUpdateRequest);

}
