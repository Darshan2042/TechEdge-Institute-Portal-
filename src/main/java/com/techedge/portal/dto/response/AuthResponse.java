package com.techedge.portal.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        AuthUserResponse user
) {
}