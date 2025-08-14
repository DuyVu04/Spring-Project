package com.example.spring_project.controller;

import com.example.spring_project.dto.request.PermissionRequest;
import com.example.spring_project.dto.request.RoleRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.PermissionResponse;
import com.example.spring_project.dto.response.RoleResponse;
import com.example.spring_project.service.PermissionService;
import com.example.spring_project.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService  roleService;

    @PostMapping
    public ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest request) {
        RoleResponse response = roleService.create(request);
        return ApiResponse.<RoleResponse>builder()
                .result(response)
                .build();
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAllRole() {
        List<RoleResponse> roles = roleService.getAll();
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roles)
                .build();
    }

    @DeleteMapping("/{role}")
    public ApiResponse<Void> deleteRole(@PathVariable String role) {
        roleService.delete(role);
        return ApiResponse.<Void>builder().build();
    }
}
