package com.example.spring_project.mapper;
import com.example.spring_project.dto.request.PermissionRequest;
import com.example.spring_project.dto.response.PermissionResponse;
import com.example.spring_project.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    

    Permission toPermission(PermissionRequest permissionRequest);
    

    PermissionResponse toPermissionResponse(Permission permission);
}
