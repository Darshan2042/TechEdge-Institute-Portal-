package com.techedge.portal.dto.response;

import com.techedge.portal.entity.enums.Role;

public record AuthUserResponse(
        Long id,
        String fullName,
        Role role
) {
}