package com.plania.dto.task;

import com.plania.model.enums.EnergyRequired;
import com.plania.model.enums.Priority;
import com.plania.model.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        LocalDate dueDate,
        LocalTime dueTime,
        Priority priority,
        EnergyRequired energyRequired,
        Integer estimatedMinutes,
        TaskStatus status,
        String categoryName,
        String categoryColor,
        Integer postponedCount,
        Integer smartScore,
        Boolean procrastinationAlert,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime completedAt
) {
}
