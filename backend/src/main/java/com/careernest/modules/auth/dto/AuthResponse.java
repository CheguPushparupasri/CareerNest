package com.careernest.modules.auth.dto;

import com.careernest.modules.user.dto.UserResponse;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserResponse user
) {
}
