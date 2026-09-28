package com.techedge.portal.controller;

import com.techedge.portal.dto.request.ChangePasswordRequest;
import com.techedge.portal.dto.request.LoginRequest;
import com.techedge.portal.dto.request.RefreshTokenRequest;
import com.techedge.portal.dto.request.RegisterRequest;
import com.techedge.portal.dto.response.ApiResponse;
import com.techedge.portal.dto.response.AuthResponse;
import com.techedge.portal.dto.response.RegisterResponse;
import com.techedge.portal.dto.response.UserResponse;
import com.techedge.portal.security.UserPrincipal;
import com.techedge.portal.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        RegisterResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Registration successful",
                        response
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Login successful",
                        response
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        AuthResponse response =
                authService.refresh(request.refreshToken());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Token refreshed successfully",
                        response
                )
        );
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            Authentication authentication
    ) {

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        UserResponse response =
                authService.getCurrentUser(principal.getId());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Profile fetched successfully",
                        response
                )
        );
    }

    @PostMapping("/change-password")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ) {

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        authService.changePassword(
                principal.getId(),
                request
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Password changed successfully",
                        null
                )
        );
    }
}