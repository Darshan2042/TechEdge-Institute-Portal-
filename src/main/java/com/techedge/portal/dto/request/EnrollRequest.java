package com.techedge.portal.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EnrollRequest(

        @NotNull(message = "Batch ID is required")
        Long batchId,

        @Size(max = 30, message = "Coupon code must not exceed 30 characters")
        String couponCode
) {
}