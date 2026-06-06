package com.plania.dto.auth;

import com.plania.dto.user.UserResponse;

public record AuthResponse(
        String token,
        String tokenType,
        UserResponse user
) {
}
