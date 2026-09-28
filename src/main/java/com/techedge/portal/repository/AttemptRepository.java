package com.techedge.portal.repository;

import com.techedge.portal.entity.Attempt;
import com.techedge.portal.entity.enums.AttemptStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {

    List<Attempt> findByStudentId(Long studentId);

    List<Attempt> findByExamId(Long examId);

    List<Attempt> findByStudentIdAndExamId(Long studentId, Long examId);

    Optional<Attempt> findByIdAndStudentId(Long id, Long studentId);

    List<Attempt> findByStatus(AttemptStatus status);
}