package com.techedge.portal.service.impl;

import com.techedge.portal.dto.request.EnrollRequest;
import com.techedge.portal.dto.request.PaymentRequest;
import com.techedge.portal.dto.request.StatusUpdateRequest;
import com.techedge.portal.dto.response.EnrollmentResponse;
import com.techedge.portal.entity.Batch;
import com.techedge.portal.entity.Enrollment;
import com.techedge.portal.entity.enums.EnrollmentStatus;
import com.techedge.portal.entity.Offer;
import com.techedge.portal.entity.Student;
import com.techedge.portal.exception.BusinessRuleException;
import com.techedge.portal.exception.ResourceNotFoundException;
import com.techedge.portal.mapper.EnrollmentMapper;
import com.techedge.portal.repository.BatchRepository;
import com.techedge.portal.repository.CourseOfferRepository;
import com.techedge.portal.repository.EnrollmentRepository;
import com.techedge.portal.repository.StudentRepository;
import com.techedge.portal.service.EnrollmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final BatchRepository batchRepository;
    private final CourseOfferRepository courseOfferRepository;

    private final Map<EnrollmentStatus, EnumSet<EnrollmentStatus>> allowedTransitions;

    public EnrollmentServiceImpl(
            EnrollmentRepository enrollmentRepository,
            StudentRepository studentRepository,
            BatchRepository batchRepository,
            CourseOfferRepository courseOfferRepository) {

        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.batchRepository = batchRepository;
        this.courseOfferRepository = courseOfferRepository;

        this.allowedTransitions = createStateMachine();
    }

    private Map<EnrollmentStatus, EnumSet<EnrollmentStatus>>
    createStateMachine() {

        Map<EnrollmentStatus, EnumSet<EnrollmentStatus>> transitions =
                new EnumMap<>(EnrollmentStatus.class);

        transitions.put(
                EnrollmentStatus.APPLIED,
                EnumSet.of(
                        EnrollmentStatus.APPROVED,
                        EnrollmentStatus.REJECTED
                )
        );

        transitions.put(
                EnrollmentStatus.APPROVED,
                EnumSet.of(
                        EnrollmentStatus.ACTIVE
                )
        );

        transitions.put(
                EnrollmentStatus.ACTIVE,
                EnumSet.of(
                        EnrollmentStatus.COMPLETED,
                        EnrollmentStatus.DROPPED
                )
        );

        transitions.put(
                EnrollmentStatus.REJECTED,
                EnumSet.noneOf(EnrollmentStatus.class)
        );

        transitions.put(
                EnrollmentStatus.COMPLETED,
                EnumSet.noneOf(EnrollmentStatus.class)
        );

        transitions.put(
                EnrollmentStatus.DROPPED,
                EnumSet.noneOf(EnrollmentStatus.class)
        );

        return transitions;
    }

    // =========================================================
    // APPLY
    // =========================================================

    @Override
    @Transactional
    public EnrollmentResponse apply(
            Long userId,
            EnrollRequest request) {

        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student profile not found"
                        ));

        Batch batch = batchRepository.findById(request.batchId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Batch not found"
                        ));

        if (batch.getStatus() == com.techedge.portal.entity.enums.BatchStatus.COMPLETED) {
            throw new BusinessRuleException(
                    "This batch has already ended"
            );
        }

        if (batch.getAvailableSeats() <= 0) {
            throw new BusinessRuleException(
                    "No seats available in this batch"
            );
        }

        if (enrollmentRepository.existsByStudentIdAndBatchId(
                student.getId(),
                batch.getId())) {

            throw new BusinessRuleException(
                    "Already applied to this batch"
            );
        }

        BigDecimal originalFee =
                batch.getCourse().getFee();

        BigDecimal payableFee = originalFee;
        String appliedOffer = null;

        if (request.couponCode() != null
                && !request.couponCode().isBlank()) {

            Offer offer = findValidOffer(
                    batch.getCourse().getId(),
                    request.couponCode()
            );

            payableFee = calculateDiscountedFee(
                    originalFee,
                    offer.getDiscountPercent()
            );

            appliedOffer = offer.getTitle();
        }

        Enrollment enrollment = new Enrollment();

        enrollment.setStudent(student);
        enrollment.setBatch(batch);
        enrollment.setStatus(EnrollmentStatus.APPLIED);
        enrollment.setAppliedAt(LocalDateTime.now());
        enrollment.setFeePaid(BigDecimal.ZERO);
        enrollment.setPaymentRef(null);
        enrollment.setRemarks(null);

        Enrollment saved =
                enrollmentRepository.save(enrollment);

        // IMPORTANT:
        // Seats are NOT decremented during application.
        // Seats are decremented only when ADMIN approves.

        return EnrollmentMapper.toResponse(
                saved,
                originalFee,
                payableFee,
                appliedOffer
        );
    }

    // =========================================================
    // MY ENROLLMENTS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments(
            Long userId) {

        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student profile not found"
                        ));

        return enrollmentRepository
                .findByStudentId(student.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // SINGLE ENROLLMENT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollment(
            Long enrollmentId,
            Long userId,
            boolean admin) {

        Enrollment enrollment =
                enrollmentRepository.findById(enrollmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Enrollment not found"
                                ));

        if (!admin) {

            Student student =
                    studentRepository.findByUserId(userId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Student profile not found"
                                    ));

            if (!enrollment.getStudent()
                    .getId()
                    .equals(student.getId())) {

                throw new com.techedge.portal.exception.ForbiddenException(
                        "You are not allowed to access this enrollment"
                );
            }
        }

        return toResponse(enrollment);
    }

    // =========================================================
    // ADMIN — ALL ENROLLMENTS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getAllEnrollments(
            EnrollmentStatus status,
            Long batchId) {

        List<Enrollment> enrollments;

        if (status != null) {

            enrollments =
                    enrollmentRepository.findByStatus(status);

        } else if (batchId != null) {

            enrollments =
                    enrollmentRepository.findByBatchId(batchId);

        } else {

            enrollments =
                    enrollmentRepository.findAll();
        }

        return enrollments
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // STUDENT WITHDRAW
    // =========================================================

    @Override
    @Transactional
    public void withdraw(
            Long enrollmentId,
            Long userId) {

        Enrollment enrollment =
                enrollmentRepository.findById(enrollmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Enrollment not found"
                                ));

        Student student =
                studentRepository.findByUserId(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student profile not found"
                                ));

        if (!enrollment.getStudent()
                .getId()
                .equals(student.getId())) {

            throw new com.techedge.portal.exception.ForbiddenException(
                    "You are not allowed to withdraw this enrollment"
            );
        }

        if (enrollment.getStatus()
                != EnrollmentStatus.APPLIED) {

            throw new BusinessRuleException(
                    "Only APPLIED enrollments can be withdrawn"
            );
        }

        enrollmentRepository.delete(enrollment);
    }

    // =========================================================
    // ADMIN — APPROVE
    // =========================================================

    @Override
    @Transactional
    public EnrollmentResponse approve(
            Long enrollmentId) {

        Enrollment enrollment =
                getEnrollmentEntity(enrollmentId);

        validateTransition(
                enrollment.getStatus(),
                EnrollmentStatus.APPROVED
        );

        Batch batch = enrollment.getBatch();

        if (batch.getAvailableSeats() <= 0) {
            throw new BusinessRuleException(
                    "No seats available in this batch"
            );
        }

        enrollment.setStatus(
                EnrollmentStatus.APPROVED
        );

        enrollment.setDecidedAt(
                LocalDateTime.now()
        );

        batch.setAvailableSeats(
                batch.getAvailableSeats() - 1
        );

        enrollmentRepository.save(enrollment);
        batchRepository.save(batch);

        return toResponse(enrollment);
    }

    // =========================================================
    // ADMIN — REJECT
    // =========================================================

    @Override
    @Transactional
    public EnrollmentResponse reject(
            Long enrollmentId) {

        Enrollment enrollment =
                getEnrollmentEntity(enrollmentId);

        validateTransition(
                enrollment.getStatus(),
                EnrollmentStatus.REJECTED
        );

        enrollment.setStatus(
                EnrollmentStatus.REJECTED
        );

        enrollment.setDecidedAt(
                LocalDateTime.now()
        );

        enrollmentRepository.save(enrollment);

        return toResponse(enrollment);
    }

    // =========================================================
    // ADMIN — GENERAL STATUS UPDATE
    // =========================================================

    @Override
    @Transactional
    public EnrollmentResponse updateStatus(
            Long enrollmentId,
            StatusUpdateRequest request) {

        Enrollment enrollment =
                getEnrollmentEntity(enrollmentId);

        EnrollmentStatus current =
                enrollment.getStatus();

        EnrollmentStatus target =
                request.status();

        validateTransition(current, target);

        enrollment.setStatus(target);

        if (request.remarks() != null) {
            enrollment.setRemarks(request.remarks());
        }

        enrollment.setDecidedAt(
                LocalDateTime.now()
        );

        // A seat is returned when an ACTIVE enrollment
        // becomes DROPPED.
        if (current == EnrollmentStatus.ACTIVE
                && target == EnrollmentStatus.DROPPED) {

            Batch batch = enrollment.getBatch();

            batch.setAvailableSeats(
                    Math.min(
                            batch.getAvailableSeats() + 1,
                            batch.getTotalSeats()
                    )
            );

            batchRepository.save(batch);
        }

        enrollmentRepository.save(enrollment);

        return toResponse(enrollment);
    }

    // =========================================================
    // ADMIN — PAYMENT
    // =========================================================

    @Override
    @Transactional
    public EnrollmentResponse recordPayment(
            Long enrollmentId,
            PaymentRequest request) {

        Enrollment enrollment =
                getEnrollmentEntity(enrollmentId);

        if (enrollment.getStatus() != EnrollmentStatus.APPROVED
                && enrollment.getStatus() != EnrollmentStatus.ACTIVE) {

            throw new BusinessRuleException(
                    "Payment can only be recorded for APPROVED or ACTIVE enrollment"
            );
        }

        enrollment.setFeePaid(request.amount());
        enrollment.setPaymentRef(request.paymentRef());

        enrollmentRepository.save(enrollment);

        return toResponse(enrollment);
    }

    // =========================================================
    // STATE MACHINE VALIDATION
    // =========================================================

    private void validateTransition(
            EnrollmentStatus current,
            EnrollmentStatus target) {

        EnumSet<EnrollmentStatus> allowed =
                allowedTransitions.getOrDefault(
                        current,
                        EnumSet.noneOf(EnrollmentStatus.class)
                );

        if (!allowed.contains(target)) {

            throw new BusinessRuleException(
                    "Illegal enrollment transition from "
                            + current
                            + " to "
                            + target
            );
        }
    }

    // =========================================================
    // FIND VALID COUPON
    // =========================================================

    private Offer findValidOffer(
            Long courseId,
            String couponCode) {

        List<com.techedge.portal.entity.CourseOffer> liveOffers =
                courseOfferRepository
                        .findLiveOffersByCourseId(
                                courseId,
                                LocalDate.now()
                        );

        return liveOffers
                .stream()
                .map(com.techedge.portal.entity.CourseOffer::getOffer)
                .filter(offer ->
                        offer.getCouponCode() != null
                                && offer.getCouponCode()
                                .equalsIgnoreCase(couponCode)
                )
                .findFirst()
                .orElseThrow(() ->
                        new BusinessRuleException(
                                "Coupon is not valid for this course"
                        )
                );
    }

    // =========================================================
    // DISCOUNT CALCULATION
    // =========================================================

    private BigDecimal calculateDiscountedFee(
            BigDecimal fee,
            BigDecimal discountPercent) {

        BigDecimal discount =
                fee.multiply(discountPercent)
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );

        return fee.subtract(discount)
                .setScale(2, RoundingMode.HALF_UP);
    }

    // =========================================================
    // FIND ENTITY
    // =========================================================

    private Enrollment getEnrollmentEntity(
            Long enrollmentId) {

        return enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Enrollment not found"
                        ));
    }

    // =========================================================
    // RESPONSE MAPPING
    // =========================================================

    private EnrollmentResponse toResponse(
            Enrollment enrollment) {

        BigDecimal originalFee =
                enrollment.getBatch()
                        .getCourse()
                        .getFee();

        return EnrollmentMapper.toResponse(
                enrollment,
                originalFee,
                originalFee,
                null
        );
    }
}