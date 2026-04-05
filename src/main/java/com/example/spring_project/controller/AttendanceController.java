package com.example.spring_project.controller;

import com.example.spring_project.dto.response.ApiResponse;
import com.example.spring_project.entity.User;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.repository.UserRepository;
import com.example.spring_project.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/attendances")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final UserRepository userRepository;

    @PostMapping("/check-in")
    public ApiResponse<String> checkIn() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        attendanceService.checkIn(user);

        return ApiResponse.<String>builder()
                .message("Check-in successful")
                .result("Success")
                .build();
    }

    @GetMapping("/streak")
    public ApiResponse<Integer> getStreak() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        return ApiResponse.<Integer>builder()
                .result(attendanceService.calculateStreak(user))
                .build();
    }

    @GetMapping("/history")
    public ApiResponse<List<LocalDate>> getHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        return ApiResponse.<List<LocalDate>>builder()
                .result(attendanceService.getAttendanceHistory(user))
                .build();
    }
}
