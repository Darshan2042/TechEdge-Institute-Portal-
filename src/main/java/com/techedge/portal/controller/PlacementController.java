package com.techedge.portal.controller;

import com.techedge.portal.dto.request.PlacementRequest;
import com.techedge.portal.dto.response.ApiResponse;
import com.techedge.portal.dto.response.PageResponse;
import com.techedge.portal.dto.response.PlacementResponse;
import com.techedge.portal.dto.response.PlacementStatsResponse;
import com.techedge.portal.service.PlacementService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/placements")
@SecurityRequirement(name = "bearerAuth")
public class PlacementController {

    private final PlacementService placementService;

    public PlacementController(PlacementService placementService) {
        this.placementService = placementService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PlacementResponse>>> getPlacements(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) BigDecimal minPackage,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<PlacementResponse> response =
                placementService.getPlacements(
                        companyId,
                        courseId,
                        year,
                        minPackage,
                        page,
                        size
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Placements fetched successfully",
                        response
                )
        );
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<PlacementStatsResponse>> getStats() {

        PlacementStatsResponse response =
                placementService.getStats();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Placement statistics fetched successfully",
                        response
                )
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PlacementResponse>> createPlacement(
            @Valid @RequestBody PlacementRequest request
    ) {
        PlacementResponse response =
                placementService.createPlacement(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Placement created successfully",
                                response
                        )
                );
    }

    @PutMapping("/{placementId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PlacementResponse>> updatePlacement(
            @PathVariable Long placementId,
            @Valid @RequestBody PlacementRequest request
    ) {
        PlacementResponse response =
                placementService.updatePlacement(
                        placementId,
                        request
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Placement updated successfully",
                        response
                )
        );
    }

    @DeleteMapping("/{placementId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlacement(
            @PathVariable Long placementId
    ) {
        placementService.deletePlacement(placementId);

        return ResponseEntity.noContent().build();
    }
}