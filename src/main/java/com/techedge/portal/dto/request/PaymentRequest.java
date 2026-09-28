package com.techedge.portal.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PaymentRequest(

        @NotNull(message = "Amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Amount must be greater than 0"
        )
        BigDecimal amount,

        @Size(max = 60, message = "Payment reference must not exceed 60 characters")
        String paymentRef
) {
}