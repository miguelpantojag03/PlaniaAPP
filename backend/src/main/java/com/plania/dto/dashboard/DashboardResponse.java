package com.plania.dto.dashboard;

import com.plania.dto.recommendation.RecommendationResponse;
import com.plania.dto.task.TaskResponse;
import com.plania.model.enums.MoodType;

import java.time.LocalDate;
import java.util.List;

public record DashboardResponse(
        String greeting,
        LocalDate currentDate,
        MoodType todayMood,
        Long pendingTasks,
        Long completedTasksToday,
        Long completedTasksTotal,
        Integer totalPoints,
        Integer currentStreak,
        RecommendationResponse recommendedTask,
        List<TaskResponse> todayTasks,
        String motivationalMessage
) {
}
