package com.plania.dto.task;

import com.plania.model.enums.EnergyRequired;
import com.plania.model.enums.Priority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record TaskRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must have at most 150 characters")
        String title,

        @Size(max = 1000, message = "Description must have at most 1000 characters")
        String description,

        @NotNull(message = "Due date is required")
        @FutureOrPresent(message = "Due date cannot be in the past")
        LocalDate dueDate,

        LocalTime dueTime,

        @NotNull(message = "Priority is required")
        Priority priority,

        @NotNull(message = "Energy required is required")
        EnergyRequired energyRequired,

        @NotNull(message = "Estimated minutes is required")
        @Min(value = 5, message = "Estimated minutes must be at least 5")
        @Max(value = 1440, message = "Estimated minutes cannot be greater than 1440")
        Integer estimatedMinutes,

        Long categoryId
) {
}
