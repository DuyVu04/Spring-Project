package com.example.spring_project.dto.response;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardResponse {
    private String name;
    private Long totalLearnedVocab;
    private Long totalToLearnVocab;
    private Integer dailyStreak;
    private List<DailyActivity> weeklyActivity;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyActivity {
        private String day;
        private Double hours;
    }
}
