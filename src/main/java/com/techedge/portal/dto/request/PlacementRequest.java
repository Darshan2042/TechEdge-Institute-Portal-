package com.techedge.portal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PlacementRequest(

        @NotNull(message = "Student ID is required")
        Long studentId,

        @NotNull(message = "Company ID is required")
        Long companyId,

        Long courseId,

        @NotBlank(message = "Job title is required")
        String jobTitle,

        @Positive(message = "Package must be greater than zero")
        BigDecimal packageLpa,

        @NotNull(message = "Placed date is required")
        LocalDate placedOn
) {
}