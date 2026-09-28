package com.techedge.portal.repository;

import com.techedge.portal.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findByCourseId(Long courseId);

    List<Exam> findByCourseIdAndActiveTrue(Long courseId);

    List<Exam> findByActiveTrue();
}