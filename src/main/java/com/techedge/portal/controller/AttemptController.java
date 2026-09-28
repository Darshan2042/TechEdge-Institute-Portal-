package com.techedge.portal.controller;

import com.techedge.portal.dto.request.SubmitAnswersRequest;
import com.techedge.portal.dto.response.ApiResponse;
import com.techedge.portal.dto.response.AttemptResultResponse;
import com.techedge.portal.dto.response.AttemptStartResponse;
import com.techedge.portal.security.UserPrincipal;
import com.techedge.portal.service.AttemptService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.techedge.portal.dto.response.AttemptSummaryResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attempts")
@SecurityRequirement(name = "bearerAuth")
public class AttemptController {

    private final AttemptService attemptService;

    public AttemptController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @PutMapping("/{id}/answers")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> saveAnswers(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody SubmitAnswersRequest request) {

        attemptService.saveAnswers(
                id,
                principal.getId(),
                request
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Answers saved successfully",
                        null
                )
        );
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<AttemptResultResponse>> submitAttempt(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody SubmitAnswersRequest request) {

        AttemptResultResponse response =
                attemptService.submitAttempt(
                        id,
                        principal.getId(),
                        request
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Attempt submitted successfully",
                        response
                )
        );
    }


    @GetMapping("/{id}/result")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<AttemptResultResponse>> getResult(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            org.springframework.security.core.Authentication authentication) {

        boolean admin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_ADMIN"));

        AttemptResultResponse response =
                attemptService.getResult(
                        id,
                        principal.getId(),
                        admin
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Attempt result fetched successfully",
                        response
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<AttemptStartResponse>> getAttempt(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        AttemptStartResponse response =
                attemptService.getAttempt(
                        id,
                        principal.getId()
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Attempt fetched successfully",
                        response
                )
        );
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<AttemptSummaryResponse>>> getMyAttempts(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<AttemptSummaryResponse> attempts =
                attemptService.getMyAttempts(
                        principal.getId()
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "My attempts fetched successfully",
                        attempts
                )
        );
    }
}