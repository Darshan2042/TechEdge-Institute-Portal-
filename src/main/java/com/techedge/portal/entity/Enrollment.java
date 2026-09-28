package com.techedge.portal.entity;

import com.techedge.portal.entity.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_student_batch",
                        columnNames = {"student_id", "batch_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnrollmentStatus status;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    @Column(name = "fee_paid", precision = 10, scale = 2)
    private BigDecimal feePaid;

    @Column(name = "payment_ref", length = 60)
    private String paymentRef;

    @Column(name = "remarks", length = 255)
    private String remarks;
}