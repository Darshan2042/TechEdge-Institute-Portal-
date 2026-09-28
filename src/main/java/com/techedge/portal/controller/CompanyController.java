package com.techedge.portal.controller;

import com.techedge.portal.dto.request.CompanyRequest;
import com.techedge.portal.dto.response.ApiResponse;
import com.techedge.portal.dto.response.CompanyResponse;
import com.techedge.portal.service.CompanyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies")
@SecurityRequirement(name = "bearerAuth")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getCompanies() {

        List<CompanyResponse> response =
                companyService.getCompanies();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Companies fetched successfully",
                        response
                )
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(
            @Valid @RequestBody CompanyRequest request
    ) {
        CompanyResponse response =
                companyService.createCompany(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Company created successfully",
                                response
                        )
                );
    }
}