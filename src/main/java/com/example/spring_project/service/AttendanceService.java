package com.example.spring_project.service;

import com.example.spring_project.entity.Attendance;
import com.example.spring_project.entity.User;
import com.example.spring_project.enums.ActionType;
import com.example.spring_project.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final ActivityLogService activityLogService;

    @Transactional
    public void checkIn(User user) {
        LocalDate today = LocalDate.now();
        if (attendanceRepository.existsByUserAndAttendanceDate(user, today)) {
            return;
        }

        Attendance attendance = Attendance.builder()
                .user(user)
                .attendanceDate(today)
                .attended(true)
                .build();

        attendanceRepository.save(attendance);
        activityLogService.logActivity(user, ActionType.CHECK_IN, "Daily check-in for " + today);
    }

    public int calculateStreak(User user) {
        List<Attendance> attendances = attendanceRepository.findByUserOrderByAttendanceDateDesc(user);
        if (attendances.isEmpty()) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate lastAttendance = attendances.get(0).getAttendanceDate();

        // If not attended today AND not attended yesterday, streak is 0
        if (!lastAttendance.equals(today) && !lastAttendance.equals(today.minusDays(1))) {
            return 0;
        }

        int streak = 0;
        LocalDate currentDay = lastAttendance;

        for (Attendance attendance : attendances) {
            if (attendance.getAttendanceDate().equals(currentDay)) {
                streak++;
                currentDay = currentDay.minusDays(1);
            } else {
                break;
            }
        }

        return streak;
    }

    public List<LocalDate> getAttendanceHistory(User user) {
        return attendanceRepository.findByUserOrderByAttendanceDateDesc(user).stream()
                .map(Attendance::getAttendanceDate)
                .toList();
    }
}
