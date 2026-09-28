package com.techedge.portal.repository;

import com.techedge.portal.entity.CourseOffer;
import com.techedge.portal.entity.id.CourseOfferId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CourseOfferRepository extends JpaRepository<CourseOffer, CourseOfferId> {

    List<CourseOffer> findByCourseId(Long courseId);

    List<CourseOffer> findByOfferId(Long offerId);

    boolean existsByCourseIdAndOfferId(Long courseId, Long offerId);

    @Query("""
        SELECT co
        FROM CourseOffer co
        JOIN FETCH co.offer o
        WHERE co.course.id = :courseId
          AND o.active = true
          AND o.validFrom <= :today
          AND o.validTo >= :today
        """)
    List<CourseOffer> findLiveOffersByCourseId(
            @Param("courseId") Long courseId,
            @Param("today") LocalDate today
    );
}