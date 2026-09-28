package com.techedge.portal.service;

import com.techedge.portal.dto.request.ChangePasswordRequest;
import com.techedge.portal.dto.request.LoginRequest;
import com.techedge.portal.dto.request.RegisterRequest;
import com.techedge.portal.dto.response.AuthResponse;
import com.techedge.portal.dto.response.RegisterResponse;
import com.techedge.portal.dto.response.UserResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(String refreshToken);

    UserResponse getCurrentUser(Long userId);

    void changePassword(Long userId, ChangePasswordRequest request);
}