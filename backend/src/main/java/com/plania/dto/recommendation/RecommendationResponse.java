package com.plania.dto.recommendation;

import com.plania.dto.task.TaskResponse;
import com.plania.model.enums.MoodType;

import java.time.LocalDateTime;

public record RecommendationResponse(
        TaskResponse task,
        Integer smartScore,
        MoodType moodType,
        String reason,
        LocalDateTime generatedAt
) {
}
