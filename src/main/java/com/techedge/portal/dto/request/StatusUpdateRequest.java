package com.techedge.portal.dto.request;

import com.techedge.portal.entity.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StatusUpdateRequest(

        @NotNull(message = "Status is required")
        EnrollmentStatus status,

        @Size(max = 255, message = "Remarks must not exceed 255 characters")
        String remarks
) {
}