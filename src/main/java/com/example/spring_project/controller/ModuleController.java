package com.example.spring_project.controller;

import com.example.spring_project.dto.request.ModuleRequest;
import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.ModuleResponse;
import com.example.spring_project.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/courses/{courseId}/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    ApiResponse<ModuleResponse> createModule(
            @PathVariable UUID courseId,
            @RequestBody ModuleRequest moduleRequest
    ) {
        return ApiResponse.<ModuleResponse>builder()
                .result(moduleService.createModule(courseId, moduleRequest))
                .build();
    }

    @GetMapping
    ApiResponse<List<ModuleResponse>> getAllModules(@PathVariable UUID courseId) {
        return ApiResponse.<List<ModuleResponse>>builder()
                .result(moduleService.getModulesByCourse(courseId))
                .build();
    }

    @PutMapping("/{moduleId}")
    ApiResponse<ModuleResponse> updateModule(
            @PathVariable UUID moduleId,
            @RequestBody ModuleRequest moduleRequest
    ) {
        return ApiResponse.<ModuleResponse>builder()
                .result(moduleService.updateModule(moduleId, moduleRequest))
                .build();
    }

    @DeleteMapping("/{moduleId}")
    ApiResponse<Void> deleteModule(@PathVariable UUID moduleId) {
        moduleService.deleteModule(moduleId);
        return ApiResponse.<Void>builder().build();
    }
}
