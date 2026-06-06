package com.plania.mapper;

import com.plania.dto.user.UserResponse;
import com.plania.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getTotalPoints(),
                user.getCurrentStreak(),
                user.getLastActivityDate(),
                user.getCreatedAt()
        );
    }
}
