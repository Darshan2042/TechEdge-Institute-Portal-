package com.techedge.portal.service;

import com.techedge.portal.dto.request.CourseRequest;
import com.techedge.portal.dto.response.CourseDetailResponse;
import com.techedge.portal.dto.response.CourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CourseService {

    Page<CourseResponse> getCourses(
            com.techedge.portal.entity.enums.CourseCategory category,
            com.techedge.portal.entity.enums.CourseLevel level,
            BigDecimal minFee,
            BigDecimal maxFee,
            String search,
            Pageable pageable
    );

    CourseDetailResponse getCourseById(Long courseId);

    CourseResponse createCourse(CourseRequest request);

    CourseResponse updateCourse(Long courseId, CourseRequest request);

    void deleteCourse(Long courseId);
}