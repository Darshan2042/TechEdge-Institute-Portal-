package com.techedge.portal.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OfferRequest(

        @NotBlank(message = "Offer title is required")
        @Size(max = 150, message = "Offer title must not exceed 150 characters")
        String title,

        String description,

        @NotNull(message = "Discount percentage is required")
        @DecimalMin(value = "0.01", message = "Discount must be greater than 0")
        @DecimalMax(value = "100.00", message = "Discount cannot exceed 100")
        BigDecimal discountPercent,

        @Size(max = 30, message = "Coupon code must not exceed 30 characters")
        String couponCode,

        @NotNull(message = "Valid from date is required")
        LocalDate validFrom,

        @NotNull(message = "Valid to date is required")
        LocalDate validTo
) {
}