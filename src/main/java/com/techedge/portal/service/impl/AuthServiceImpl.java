package com.techedge.portal.service.impl;

import com.techedge.portal.dto.request.ChangePasswordRequest;
import com.techedge.portal.dto.request.LoginRequest;
import com.techedge.portal.dto.request.RegisterRequest;
import com.techedge.portal.dto.response.AuthResponse;
import com.techedge.portal.dto.response.AuthUserResponse;
import com.techedge.portal.dto.response.RegisterResponse;
import com.techedge.portal.dto.response.UserResponse;
import com.techedge.portal.entity.enums.Role;
import com.techedge.portal.entity.Student;
import com.techedge.portal.entity.User;
import com.techedge.portal.exception.BusinessRuleException;
import com.techedge.portal.exception.ResourceNotFoundException;
import com.techedge.portal.exception.UnauthorizedException;
import com.techedge.portal.mapper.UserMapper;
import com.techedge.portal.repository.StudentRepository;
import com.techedge.portal.repository.UserRepository;
import com.techedge.portal.security.JwtTokenProvider;
import com.techedge.portal.service.AuthService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    public AuthServiceImpl(
            UserRepository userRepository,
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
    }

    // =========================================================
    // REGISTER
    // =========================================================

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        // Check duplicate email
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException(
                    "Email is already registered"
            );
        }

        // Create User
        User user = new User();

        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );
        user.setPhone(request.phone());

        // IMPORTANT:
        // Role must never come from request.
        // Every public registration creates STUDENT.
        user.setRole(Role.STUDENT);

        // New users are active
        user.setActive(true);

        User savedUser = userRepository.save(user);

        // Create Student profile
        Student student = new Student();

        student.setUser(savedUser);
        student.setQualification(request.qualification());
        student.setGraduationYear(request.graduationYear());
        student.setCity(request.city());

        studentRepository.save(student);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {

        /*
         * Always return the same message for:
         * - email does not exist
         * - wrong password
         * - inactive account
         *
         * This prevents account enumeration.
         */

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "Invalid email or password"
                        )
                );

        // Check whether account is active
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new UnauthorizedException(
                    "Invalid email or password"
            );
        }

        // Check password
        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {
            throw new UnauthorizedException(
                    "Invalid email or password"
            );
        }

        // Generate access token
        String accessToken =
                jwtTokenProvider.generateAccessToken(user);

        // Generate refresh token
        String refreshToken =
                jwtTokenProvider.generateRefreshToken(user);

        /*
         * Login response only contains:
         * id, fullName and role
         */
        AuthUserResponse authUser = new AuthUserResponse(
                user.getId(),
                user.getFullName(),
                user.getRole()
        );

        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtTokenProvider.getAccessExpirySeconds(),
                authUser
        );
    }

    // =========================================================
    // REFRESH TOKEN
    // =========================================================

    @Override
    @Transactional
    public AuthResponse refresh(String refreshToken) {

        // Check empty token
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException(
                    "Invalid refresh token"
            );
        }

        // Validate JWT
        if (!jwtTokenProvider.isTokenValid(refreshToken)) {
            throw new UnauthorizedException(
                    "Invalid refresh token"
            );
        }

        Long userId;

        try {
            userId = jwtTokenProvider.extractUserId(refreshToken);
        } catch (Exception ex) {
            throw new UnauthorizedException(
                    "Invalid refresh token"
            );
        }

        // Find user from database
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "Invalid refresh token"
                        )
                );

        // Important:
        // Do not trust JWT claims blindly.
        // Check current DB account status.
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new UnauthorizedException(
                    "Invalid refresh token"
            );
        }

        // Generate new access token
        String newAccessToken =
                jwtTokenProvider.generateAccessToken(user);

        // Generate new refresh token
        String newRefreshToken =
                jwtTokenProvider.generateRefreshToken(user);

        AuthUserResponse authUser = new AuthUserResponse(
                user.getId(),
                user.getFullName(),
                user.getRole()
        );

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                jwtTokenProvider.getAccessExpirySeconds(),
                authUser
        );
    }

    // =========================================================
    // GET CURRENT USER
    // =========================================================

    @Override
    @Transactional
    public UserResponse getCurrentUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        // Inactive account cannot access profile
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new UnauthorizedException(
                    "User account is inactive"
            );
        }

        return userMapper.toResponse(user);
    }

    // =========================================================
    // CHANGE PASSWORD
    // =========================================================

    @Override
    @Transactional
    public void changePassword(
            Long userId,
            ChangePasswordRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        // Check account status
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new UnauthorizedException(
                    "User account is inactive"
            );
        }

        // Verify current password
        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPasswordHash()
        )) {
            throw new BusinessRuleException(
                    "Current password is incorrect"
            );
        }

        // Prevent same password
        if (passwordEncoder.matches(
                request.newPassword(),
                user.getPasswordHash()
        )) {
            throw new BusinessRuleException(
                    "New password must be different from current password"
            );
        }

        // Encode new password
        String encodedPassword =
                passwordEncoder.encode(
                        request.newPassword()
                );

        user.setPasswordHash(encodedPassword);

        userRepository.save(user);
    }
}