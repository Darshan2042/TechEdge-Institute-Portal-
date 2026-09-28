package com.techedge.portal.repository;

import com.techedge.portal.entity.Enrollment;
import com.techedge.portal.entity.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByBatchId(Long batchId);

    List<Enrollment> findByStatus(EnrollmentStatus status);

    Optional<Enrollment> findByStudentIdAndBatchId(
            Long studentId,
            Long batchId
    );

    boolean existsByStudentIdAndBatchId(
            Long studentId,
            Long batchId
    );

    @Query("""
            SELECT e
            FROM Enrollment e
            WHERE e.batch.course.id = :courseId
            """)
    boolean existsByCourseId(Long courseId);

    @Query("""
        SELECT COUNT(e) > 0
        FROM Enrollment e
        WHERE e.student.id = :studentId
          AND e.batch.course.id = :courseId
          AND e.status = :status
        """)
    boolean existsByStudentIdAndCourseIdAndStatus(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId,
            @Param("status") EnrollmentStatus status
    );
}