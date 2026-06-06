package com.plania.dto.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        Integer totalPoints,
        Integer currentStreak,
        LocalDate lastActivityDate,
        LocalDateTime createdAt
) {
}
