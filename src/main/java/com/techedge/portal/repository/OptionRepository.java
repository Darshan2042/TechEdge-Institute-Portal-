package com.techedge.portal.repository;

import com.techedge.portal.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findByQuestionId(Long questionId);

    List<Option> findByQuestionIdAndCorrectTrue(Long questionId);
}