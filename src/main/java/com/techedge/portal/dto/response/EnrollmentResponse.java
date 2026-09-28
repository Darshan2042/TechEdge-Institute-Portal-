package com.techedge.portal.dto.response;

import com.techedge.portal.entity.enums.EnrollmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EnrollmentResponse(

        Long id,

        String batchCode,

        String courseTitle,

        EnrollmentStatus status,

        LocalDateTime appliedAt,

        BigDecimal originalFee,

        BigDecimal payableFee,

        String appliedOffer,

        LocalDateTime decidedAt,

        BigDecimal feePaid,

        String paymentRef,

        String remarks
) {
}