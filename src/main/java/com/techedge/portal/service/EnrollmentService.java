package com.techedge.portal.service;

import com.techedge.portal.dto.request.EnrollRequest;
import com.techedge.portal.dto.request.PaymentRequest;
import com.techedge.portal.dto.request.StatusUpdateRequest;
import com.techedge.portal.dto.response.EnrollmentResponse;
import com.techedge.portal.entity.enums.EnrollmentStatus;

import java.util.List;

public interface EnrollmentService {

    EnrollmentResponse apply(
            Long userId,
            EnrollRequest request
    );

    List<EnrollmentResponse> getMyEnrollments(
            Long userId
    );

    EnrollmentResponse getEnrollment(
            Long enrollmentId,
            Long userId,
            boolean admin
    );

    List<EnrollmentResponse> getAllEnrollments(
            EnrollmentStatus status,
            Long batchId
    );

    void withdraw(
            Long enrollmentId,
            Long userId
    );

    EnrollmentResponse approve(
            Long enrollmentId
    );

    EnrollmentResponse reject(
            Long enrollmentId
    );

    EnrollmentResponse updateStatus(
            Long enrollmentId,
            StatusUpdateRequest request
    );

    EnrollmentResponse recordPayment(
            Long enrollmentId,
            PaymentRequest request
    );
}