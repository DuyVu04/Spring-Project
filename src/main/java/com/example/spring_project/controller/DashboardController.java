package com.example.spring_project.controller;

import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.dto.response.AdminDashboardResponse;
import com.example.spring_project.dto.response.StudentDashboardResponse;
import com.example.spring_project.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboards")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/student")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<StudentDashboardResponse> getStudentDashboard() {
        return ApiResponse.<StudentDashboardResponse>builder()
                .result(dashboardService.getStudentDashboard())
                .build();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AdminDashboardResponse> getAdminDashboard() {
        return ApiResponse.<AdminDashboardResponse>builder()
                .result(dashboardService.getAdminDashboard())
                .build();
    }
}
