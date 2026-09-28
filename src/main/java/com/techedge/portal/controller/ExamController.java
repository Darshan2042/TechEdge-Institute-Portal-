package com.techedge.portal.controller;

import com.techedge.portal.dto.request.ExamRequest;
import com.techedge.portal.dto.request.QuestionRequest;
import com.techedge.portal.dto.response.ApiResponse;
import com.techedge.portal.dto.response.AttemptSummaryResponse;
import com.techedge.portal.dto.response.ExamResponse;
import com.techedge.portal.security.UserPrincipal;
import com.techedge.portal.service.ExamService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.techedge.portal.dto.response.AttemptStartResponse;
import com.techedge.portal.service.AttemptService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exams")
@SecurityRequirement(name = "bearerAuth")
public class ExamController {

    private final ExamService examService;
    private final AttemptService attemptService;

    public ExamController(ExamService examService, AttemptService attemptService) {
        this.examService = examService;
        this.attemptService = attemptService;
    }

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<ExamResponse>>> getExams(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<ExamResponse> exams =
                examService.getExams(principal.getId());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Exams fetched successfully",
                        exams
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ExamResponse>> getExamById(
            @PathVariable Long id) {

        ExamResponse response =
                examService.getExamById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Exam fetched successfully",
                        response
                )
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ExamResponse>> createExam(
            @Valid @RequestBody ExamRequest request) {

        ExamResponse response =
                examService.createExam(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Exam created successfully",
                                response
                        )
                );
    }

    @PostMapping("/{id}/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> addQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequest request) {

        examService.addQuestion(id, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Question added successfully",
                                null
                        )
                );
    }


    @PostMapping("/{id}/attempts")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<AttemptStartResponse>> startAttempt(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        AttemptStartResponse response =
                attemptService.startAttempt(
                        id,
                        principal.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Exam attempt started successfully",
                                response
                        )
                );
    }

    @GetMapping("/{id}/attempts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AttemptSummaryResponse>>> getExamAttempts(
            @PathVariable Long id) {

        List<AttemptSummaryResponse> attempts =
                attemptService.getExamAttempts(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Exam attempts fetched successfully",
                        attempts
                )
        );
    }
}