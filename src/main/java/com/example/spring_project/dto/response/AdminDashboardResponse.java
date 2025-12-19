package com.example.spring_project.dto.response;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    private Long totalStudents;
    private Long totalCourses;
    private Long totalModules;
    private Long totalLessons;
    private Map<String, Long> monthlyGrowth;
    private List<CategoryStat> popularCategories;
    private List<RecentActivityDTO> recentActivities;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryStat {
        private String label;
        private Integer percent;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivityDTO {
        private String id;
        private String title;
        private String desc;
        private String time;
        private String type;
    }
}
