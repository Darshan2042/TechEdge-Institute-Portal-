package com.techedge.portal.controller;

import com.techedge.portal.dto.request.BatchRequest;
import com.techedge.portal.dto.response.ApiResponse;
import com.techedge.portal.dto.response.BatchResponse;
import com.techedge.portal.entity.enums.BatchMode;
import com.techedge.portal.entity.enums.BatchStatus;
import com.techedge.portal.service.BatchService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/batches")
@SecurityRequirement(name = "bearerAuth")
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    // Public: Get all batches with optional filters
    @GetMapping
    public ResponseEntity<ApiResponse<List<BatchResponse>>> getBatches(
            @RequestParam(required = false) BatchStatus status,
            @RequestParam(required = false) BatchMode mode) {

        List<BatchResponse> response =
                batchService.getBatches(status, mode);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Batches fetched successfully",
                        response
                )
        );
    }

    // Public: Get batches for a specific course
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<BatchResponse>>> getBatchesByCourse(
            @PathVariable Long courseId) {

        List<BatchResponse> response =
                batchService.getBatchesByCourse(courseId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Course batches fetched successfully",
                        response
                )
        );
    }

    // ADMIN only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BatchResponse>> createBatch(
            @Valid @RequestBody BatchRequest request) {

        BatchResponse response = batchService.createBatch(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Batch created successfully",
                                response
                        )
                );
    }

    // ADMIN only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BatchResponse>> updateBatch(
            @PathVariable Long id,
            @Valid @RequestBody BatchRequest request) {

        BatchResponse response =
                batchService.updateBatch(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Batch updated successfully",
                        response
                )
        );
    }
}