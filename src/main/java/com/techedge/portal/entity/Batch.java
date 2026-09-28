package com.techedge.portal.entity;

import com.techedge.portal.entity.enums.BatchMode;
import com.techedge.portal.entity.enums.BatchStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "batches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "batch_code", nullable = false, unique = true, length = 30)
    private String batchCode;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "timing", length = 50)
    private String timing;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private BatchMode mode;

    @Column(name = "trainer_name", length = 100)
    private String trainerName;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BatchStatus status;
}