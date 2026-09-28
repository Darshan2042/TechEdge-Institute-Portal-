package com.techedge.portal.mapper;

import com.techedge.portal.dto.response.EnrollmentResponse;
import com.techedge.portal.entity.Enrollment;

import java.math.BigDecimal;

public class EnrollmentMapper {

    private EnrollmentMapper() {
    }

    public static EnrollmentResponse toResponse(
            Enrollment enrollment,
            BigDecimal originalFee,
            BigDecimal payableFee,
            String appliedOffer) {

        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getBatch().getBatchCode(),
                enrollment.getBatch().getCourse().getTitle(),
                enrollment.getStatus(),
                enrollment.getAppliedAt(),
                originalFee,
                payableFee,
                appliedOffer,
                enrollment.getDecidedAt(),
                enrollment.getFeePaid(),
                enrollment.getPaymentRef(),
                enrollment.getRemarks()
        );
    }
}