package com.techedge.portal.dto.response;

import com.techedge.portal.entity.enums.CourseCategory;
import com.techedge.portal.entity.enums.CourseLevel;

import java.math.BigDecimal;

public record CourseResponse(
        Long id,
        String code,
        String title,
        CourseCategory category,
        CourseLevel level,
        Integer durationWeeks,
        BigDecimal fee,
        BigDecimal discountedFee,
        String activeOfferTitle,
        Long upcomingBatchCount
) {
}