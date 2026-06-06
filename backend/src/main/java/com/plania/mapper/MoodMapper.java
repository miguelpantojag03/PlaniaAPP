package com.plania.mapper;

import com.plania.dto.mood.MoodResponse;
import com.plania.model.Mood;
import org.springframework.stereotype.Component;

@Component
public class MoodMapper {

    public MoodResponse toResponse(Mood mood) {
        return new MoodResponse(
                mood.getId(),
                mood.getMoodType(),
                mood.getNote(),
                mood.getDate(),
                mood.getCreatedAt()
        );
    }
}
