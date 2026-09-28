package com.techedge.portal.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExamRequest(

        @NotNull(message = "Course ID is required")
        Long courseId,

        @NotBlank(message = "Exam title is required")
        String title,

        @NotNull(message = "Duration is required")
        @Min(value = 1, message = "Duration must be at least 1 minute")
        Integer durationMinutes,

        @NotNull(message = "Total marks are required")
        @Min(value = 1, message = "Total marks must be at least 1")
        Integer totalMarks,

        @NotNull(message = "Passing marks are required")
        @Min(value = 1, message = "Passing marks must be at least 1")
        Integer passingMarks,

        Boolean active
) {
}