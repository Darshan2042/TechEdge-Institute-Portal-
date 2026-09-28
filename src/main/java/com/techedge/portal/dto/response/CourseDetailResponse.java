package com.techedge.portal.dto.response;

import com.techedge.portal.entity.enums.CourseCategory;
import com.techedge.portal.entity.enums.CourseLevel;

import java.math.BigDecimal;
import java.util.List;

public record CourseDetailResponse(
        Long id,
        String code,
        String title,
        String description,
        CourseCategory category,
        CourseLevel level,
        Integer durationWeeks,
        BigDecimal fee,
        BigDecimal discountedFee,
        List<TopicResponse> topics,
        List<BatchResponse> batches,
        List<OfferResponse> offers,
        BigDecimal averageRating,
        Long feedbackCount
) {
}