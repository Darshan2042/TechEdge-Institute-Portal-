package com.techedge.portal.repository;

import com.techedge.portal.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByAttemptId(Long attemptId);

    Optional<Answer> findByAttemptIdAndQuestionId(
            Long attemptId,
            Long questionId
    );

    boolean existsByAttemptIdAndQuestionId(
            Long attemptId,
            Long questionId
    );
}