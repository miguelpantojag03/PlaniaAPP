package com.plania.dto.mood;

import com.plania.model.enums.MoodType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MoodResponse(
        Long id,
        MoodType moodType,
        String note,
        LocalDate date,
        LocalDateTime createdAt
) {
}
