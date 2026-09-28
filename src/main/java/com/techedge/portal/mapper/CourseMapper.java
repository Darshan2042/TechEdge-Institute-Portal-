package com.techedge.portal.mapper;

import com.techedge.portal.dto.request.CourseRequest;
import com.techedge.portal.dto.response.CourseResponse;
import com.techedge.portal.entity.Course;

import java.math.BigDecimal;

public class CourseMapper {

    private CourseMapper() {
    }

    public static Course toEntity(CourseRequest request) {
        Course course = new Course();

        course.setCode(request.code());
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setCategory(request.category());
        course.setDurationWeeks(request.durationWeeks());
        course.setFee(request.fee());
        course.setLevel(request.level());

        return course;
    }

    public static void updateEntity(Course course, CourseRequest request) {
        course.setCode(request.code());
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setCategory(request.category());
        course.setDurationWeeks(request.durationWeeks());
        course.setFee(request.fee());
        course.setLevel(request.level());
    }

    public static CourseResponse toResponse(
            Course course,
            BigDecimal discountedFee,
            String activeOfferTitle,
            Long upcomingBatchCount
    ) {
        return new CourseResponse(
                course.getId(),
                course.getCode(),
                course.getTitle(),
                course.getCategory(),
                course.getLevel(),
                course.getDurationWeeks(),
                course.getFee(),
                discountedFee,
                activeOfferTitle,
                upcomingBatchCount
        );
    }
}