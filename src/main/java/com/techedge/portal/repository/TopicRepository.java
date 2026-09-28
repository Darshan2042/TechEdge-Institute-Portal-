package com.techedge.portal.repository;

import com.techedge.portal.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findByCourseIdOrderBySortOrderAsc(Long courseId);
}