package com.techedge.portal.repository;

import com.techedge.portal.entity.Course;
import com.techedge.portal.entity.enums.CourseCategory;
import com.techedge.portal.entity.enums.CourseLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByActiveTrue();

    List<Course> findByCategoryAndActiveTrue(CourseCategory category);

    Optional<Course> findByCode(String code);

    boolean existsByCode(String code);

    @Query("""
        SELECT c
        FROM Course c
        WHERE c.active = true
          AND (:category IS NULL OR c.category = :category)
          AND (:level IS NULL OR c.level = :level)
          AND (:minFee IS NULL OR c.fee >= :minFee)
          AND (:maxFee IS NULL OR c.fee <= :maxFee)
          AND (
                :search IS NULL
                OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))
              )
        """)
    Page<Course> searchActiveCourses(
            @Param("category") CourseCategory category,
            @Param("level") CourseLevel level,
            @Param("minFee") BigDecimal minFee,
            @Param("maxFee") BigDecimal maxFee,
            @Param("search") String search,
            Pageable pageable
    );
}