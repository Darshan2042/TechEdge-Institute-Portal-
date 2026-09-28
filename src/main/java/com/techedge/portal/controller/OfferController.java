package com.techedge.portal.controller;

import com.techedge.portal.dto.request.OfferRequest;
import com.techedge.portal.dto.response.ApiResponse;
import com.techedge.portal.dto.response.OfferResponse;
import com.techedge.portal.service.OfferService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.techedge.portal.dto.response.BatchResponse;
import com.techedge.portal.service.BatchService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/offers")
@SecurityRequirement(name = "bearerAuth")
public class OfferController {

    private final OfferService offerService;
    private final BatchService batchService;

    public OfferController(OfferService offerService, BatchService batchService) {
        this.offerService = offerService;
        this.batchService = batchService;
    }

    // Public: Live offers only
    @GetMapping
    public ResponseEntity<ApiResponse<List<OfferResponse>>> getLiveOffers() {

        List<OfferResponse> response =
                offerService.getLiveOffers();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Live offers fetched successfully",
                        response
                )
        );
    }

    // ADMIN only: All offers including inactive/expired
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OfferResponse>>> getAllOffers() {

        List<OfferResponse> response =
                offerService.getAllOffers();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "All offers fetched successfully",
                        response
                )
        );
    }

    // ADMIN only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OfferResponse>> createOffer(
            @Valid @RequestBody OfferRequest request) {

        OfferResponse response =
                offerService.createOffer(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Offer created successfully",
                                response
                        )
                );
    }

    // ADMIN only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OfferResponse>> updateOffer(
            @PathVariable Long id,
            @Valid @RequestBody OfferRequest request) {

        OfferResponse response =
                offerService.updateOffer(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Offer updated successfully",
                        response
                )
        );
    }

    // ADMIN only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateOffer(
            @PathVariable Long id) {

        offerService.deactivateOffer(id);

        return ResponseEntity.noContent().build();
    }

}