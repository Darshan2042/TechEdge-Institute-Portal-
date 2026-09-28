package com.techedge.portal.repository;

import com.techedge.portal.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository
        extends JpaRepository<Feedback, Long> {

    List<Feedback> findByApprovedTrue();

    List<Feedback> findByApprovedFalse();

    boolean existsByStudentIdAndCourseId(
            Long studentId,
            Long courseId
    );

    Optional<Feedback> findByIdAndStudentId(
            Long feedbackId,
            Long studentId
    );
}