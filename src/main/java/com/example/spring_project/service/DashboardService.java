package com.example.spring_project.service;

import com.example.spring_project.dto.response.AdminDashboardResponse;
import com.example.spring_project.dto.response.StudentDashboardResponse;
import com.example.spring_project.entity.User;
import com.example.spring_project.enums.WordStatus;
import com.example.spring_project.exception.CustomException;
import com.example.spring_project.exception.ErrorCode;
import com.example.spring_project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final VocabularyRepository vocabularyRepository;
    private final ActivityLogRepository activityLogRepository;
    private final AttendanceService attendanceService;

    @Cacheable(value = "dashboard:student", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()", unless = "#result == null")
    public StudentDashboardResponse getStudentDashboard() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));


        long totalLearned = vocabularyRepository.countByUserIdAndStatus(user.getId(), WordStatus.MASTERED);
        long totalToLearn = vocabularyRepository.countByUserIdAndStatus(user.getId(), WordStatus.TO_LEARN);


        int dailyStreak = attendanceService.calculateStreak(user);


        List<StudentDashboardResponse.DailyActivity> weeklyActivity = new ArrayList<>();

        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        Random random = new Random();
        for (String day : days) {
            weeklyActivity.add(new StudentDashboardResponse.DailyActivity(day, 2.0 + random.nextDouble() * 3));
        }

        return StudentDashboardResponse.builder()
                .name(user.getFirstName())
                .totalLearnedVocab(totalLearned)
                .totalToLearnVocab(totalToLearn)
                .dailyStreak(dailyStreak)
                .weeklyActivity(weeklyActivity)
                .build();
    }

    @Cacheable(value = "dashboard:admin", key = "'stats'", unless = "#result == null")
    public AdminDashboardResponse getAdminDashboard() {
        long totalStudents = userRepository.count();
        long totalCourses = courseRepository.count();
        long totalModules = moduleRepository.count();
        long totalLessons = lessonRepository.count();


        List<AdminDashboardResponse.CategoryStat> categories = List.of(
                new AdminDashboardResponse.CategoryStat("IELTS Prep", 85),
                new AdminDashboardResponse.CategoryStat("Academic Writing", 62),
                new AdminDashboardResponse.CategoryStat("Business English", 45),
                new AdminDashboardResponse.CategoryStat("Grammar Mastery", 38)
        );


        var recentLogs = activityLogRepository.findTop10ByOrderByCreatedAtDesc();
        List<AdminDashboardResponse.RecentActivityDTO> recentActivities = recentLogs.stream()
                .map(log -> AdminDashboardResponse.RecentActivityDTO.builder()
                        .id(log.getId().toString())
                        .title(log.getActionType().name())
                        .desc(log.getDescription())
                        .time("Recent")
                        .type(log.getActionType().name())
                        .build())
                .collect(Collectors.toList());

        Map<String, Long> growth = new LinkedHashMap<>();
        growth.put("JAN", 400L);
        growth.put("FEB", 600L);
        growth.put("MAR", 800L);

        return AdminDashboardResponse.builder()
                .totalStudents(totalStudents)
                .totalCourses(totalCourses)
                .totalModules(totalModules)
                .totalLessons(totalLessons)
                .popularCategories(categories)
                .recentActivities(recentActivities)
                .monthlyGrowth(growth)
                .build();
    }
}
