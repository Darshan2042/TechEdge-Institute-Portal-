package com.techedge.portal.dto.request;

import com.techedge.portal.entity.enums.CourseCategory;
import com.techedge.portal.entity.enums.CourseLevel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CourseRequest(

        @NotBlank(message = "Course code is required")
        @Size(max = 20, message = "Course code must not exceed 20 characters")
        String code,

        @NotBlank(message = "Course title is required")
        @Size(max = 150, message = "Course title must not exceed 150 characters")
        String title,

        String description,

        @NotNull(message = "Course category is required")
        CourseCategory category,

        @NotNull(message = "Duration in weeks is required")
        @Min(value = 1, message = "Duration must be at least 1 week")
        Integer durationWeeks,

        @NotNull(message = "Course fee is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Fee must be greater than 0")
        BigDecimal fee,

        @NotNull(message = "Course level is required")
        CourseLevel level
) {
}