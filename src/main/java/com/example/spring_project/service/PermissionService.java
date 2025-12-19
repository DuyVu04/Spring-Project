package com.example.spring_project.service;

import com.example.spring_project.dto.request.PermissionRequest;
import com.example.spring_project.dto.response.PermissionResponse;
import com.example.spring_project.entity.Permission;
import com.example.spring_project.mapper.PermissionMapper;
import com.example.spring_project.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE ,makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @CacheEvict(value = "permissions", allEntries = true)
    public PermissionResponse create(PermissionRequest permissionRequest) {

        Permission permission = permissionMapper.toPermission(permissionRequest);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    @Cacheable(value = "permissions", key = "'all'", unless = "#result == null")
    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }

    @CacheEvict(value = "permissions", allEntries = true)
    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }
}
