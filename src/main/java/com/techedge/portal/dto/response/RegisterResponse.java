package com.techedge.portal.dto.response;

import com.techedge.portal.entity.enums.Role;

public record RegisterResponse(
        Long userId,
        String email,
        Role role
) {
}