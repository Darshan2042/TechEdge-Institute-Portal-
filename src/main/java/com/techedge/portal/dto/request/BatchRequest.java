package com.techedge.portal.dto.request;

import com.techedge.portal.entity.enums.BatchMode;
import com.techedge.portal.entity.enums.BatchStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record BatchRequest(

        @NotNull(message = "Course ID is required")
        Long courseId,

        @NotBlank(message = "Batch code is required")
        @Size(max = 30, message = "Batch code must not exceed 30 characters")
        String batchCode,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate,

        @Size(max = 50, message = "Timing must not exceed 50 characters")
        String timing,

        @NotNull(message = "Batch mode is required")
        BatchMode mode,

        @Size(max = 100, message = "Trainer name must not exceed 100 characters")
        String trainerName,

        @NotNull(message = "Total seats are required")
        @Min(value = 1, message = "Total seats must be at least 1")
        Integer totalSeats,

        @NotNull(message = "Available seats are required")
        @Min(value = 0, message = "Available seats cannot be negative")
        Integer availableSeats,

        @NotNull(message = "Batch status is required")
        BatchStatus status
) {
}