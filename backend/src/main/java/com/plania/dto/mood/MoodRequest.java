package com.plania.dto.mood;

import com.plania.model.enums.MoodType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MoodRequest(
        @NotNull(message = "Mood type is required")
        MoodType moodType,

        @Size(max = 500, message = "Note must have at most 500 characters")
        String note,

        LocalDate date
) {
}
