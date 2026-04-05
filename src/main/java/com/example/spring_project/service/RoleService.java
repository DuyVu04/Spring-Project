package com.example.spring_project.service;

import com.example.spring_project.dto.request.RoleRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.RoleResponse;
import com.example.spring_project.mapper.RoleMapper;
import com.example.spring_project.repository.PermissionRepository;
import com.example.spring_project.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE ,makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionRepository permissionRepository;

    @CacheEvict(value = "roles", allEntries = true)
    public RoleResponse create (RoleRequest request){
        var role =roleMapper.toRole(request);
        var permission = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permission));
        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);

    }

    @Cacheable(value = "roles", key = "'all'", unless = "#result == null")
    public List<RoleResponse> getAll(){
        var roles =roleRepository.findAll();
        return roles.stream().map(roleMapper::toRoleResponse).toList();
    }

    @CacheEvict(value = "roles", allEntries = true)
    public void delete(String role){
        roleRepository.deleteById(role);
    }

}
