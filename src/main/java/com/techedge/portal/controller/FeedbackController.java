package com.techedge.portal.controller;

import com.techedge.portal.dto.request.FeedbackRequest;
import com.techedge.portal.dto.response.ApiResponse;
import com.techedge.portal.dto.response.FeedbackResponse;
import com.techedge.portal.security.UserPrincipal;
import com.techedge.portal.service.FeedbackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feedback")
@SecurityRequirement(name = "bearerAuth")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getApprovedFeedback() {

        List<FeedbackResponse> response =
                feedbackService.getApprovedFeedback();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Feedback fetched successfully",
                        response
                )
        );
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getPendingFeedback() {

        List<FeedbackResponse> response =
                feedbackService.getPendingFeedback();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Pending feedback fetched successfully",
                        response
                )
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<FeedbackResponse>> submitFeedback(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody FeedbackRequest request
    ) {
        FeedbackResponse response =
                feedbackService.submitFeedback(
                        principal.getId(),
                        request
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Feedback submitted successfully",
                                response
                        )
                );
    }

    @PatchMapping("/{feedbackId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FeedbackResponse>> approveFeedback(
            @PathVariable Long feedbackId
    ) {
        FeedbackResponse response =
                feedbackService.approveFeedback(feedbackId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Feedback approved successfully",
                        response
                )
        );
    }

    @DeleteMapping("/{feedbackId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<Void> deleteFeedback(
            @PathVariable Long feedbackId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        boolean admin = principal.getAuthorities()
                .stream()
                .anyMatch(
                        authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN")
                );

        feedbackService.deleteFeedback(
                feedbackId,
                principal.getId(),
                admin
        );

        return ResponseEntity.noContent().build();
    }
}