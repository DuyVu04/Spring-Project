package com.example.spring_project.mapper;
import com.example.spring_project.dto.request.PermissionRequest;
import com.example.spring_project.dto.request.RoleRequest;
import com.example.spring_project.dto.response.PermissionResponse;
import com.example.spring_project.dto.response.RoleResponse;
import com.example.spring_project.entity.Permission;
import com.example.spring_project.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions",ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
