package com.techedge.portal.controller;

import com.techedge.portal.dto.request.EnrollRequest;
import com.techedge.portal.dto.request.PaymentRequest;
import com.techedge.portal.dto.request.StatusUpdateRequest;
import com.techedge.portal.dto.response.ApiResponse;
import com.techedge.portal.dto.response.EnrollmentResponse;
import com.techedge.portal.entity.enums.EnrollmentStatus;
import com.techedge.portal.security.UserPrincipal;
import com.techedge.portal.service.EnrollmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
@SecurityRequirement(name = "bearerAuth")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // =========================
    // STUDENT
    // =========================

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> apply(
            @Valid @RequestBody EnrollRequest request,
            Authentication authentication) {

        Long userId = getUserId(authentication);

        EnrollmentResponse response =
                enrollmentService.apply(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Enrollment application submitted successfully",
                        response
                ));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyEnrollments(
            Authentication authentication) {

        Long userId = getUserId(authentication);

        List<EnrollmentResponse> response =
                enrollmentService.getMyEnrollments(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Enrollments fetched successfully",
                        response
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getUserId(authentication);

        enrollmentService.withdraw(id, userId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Enrollment withdrawn successfully",
                        null
                )
        );
    }

    // =========================
    // STUDENT + ADMIN
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> getEnrollment(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        boolean admin = isAdmin(authentication);

        EnrollmentResponse response =
                enrollmentService.getEnrollment(
                        id,
                        userId,
                        admin
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Enrollment fetched successfully",
                        response
                )
        );
    }

    // =========================
    // ADMIN
    // =========================

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getAllEnrollments(
            @RequestParam(required = false) EnrollmentStatus status,
            @RequestParam(required = false) Long batchId) {

        List<EnrollmentResponse> response =
                enrollmentService.getAllEnrollments(
                        status,
                        batchId
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Enrollments fetched successfully",
                        response
                )
        );
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> approve(
            @PathVariable Long id) {

        EnrollmentResponse response =
                enrollmentService.approve(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Enrollment approved successfully",
                        response
                )
        );
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> reject(
            @PathVariable Long id) {

        EnrollmentResponse response =
                enrollmentService.reject(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Enrollment rejected successfully",
                        response
                )
        );
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {

        EnrollmentResponse response =
                enrollmentService.updateStatus(
                        id,
                        request
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Enrollment status updated successfully",
                        response
                )
        );
    }

    @PostMapping("/{id}/payment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> recordPayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequest request) {

        EnrollmentResponse response =
                enrollmentService.recordPayment(
                        id,
                        request
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Payment recorded successfully",
                        response
                )
        );
    }

    // =========================
    // HELPERS
    // =========================

    private Long getUserId(Authentication authentication) {

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        return principal.getId();
    }

    private boolean isAdmin(Authentication authentication) {

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );
    }
}