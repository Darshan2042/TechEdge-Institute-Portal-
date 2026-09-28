package com.techedge.portal.mapper;

import com.techedge.portal.dto.response.UserResponse;
import com.techedge.portal.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getActive()
        );
    }
}