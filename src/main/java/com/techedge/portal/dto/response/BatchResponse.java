package com.techedge.portal.dto.response;

import com.techedge.portal.entity.enums.BatchMode;
import com.techedge.portal.entity.enums.BatchStatus;

import java.time.LocalDate;

public record BatchResponse(
        Long id,
        String batchCode,
        LocalDate startDate,
        LocalDate endDate,
        String timing,
        BatchMode mode,
        String trainerName,
        Integer availableSeats,
        Integer totalSeats,
        BatchStatus status
) {
}