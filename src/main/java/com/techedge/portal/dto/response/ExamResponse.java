package com.techedge.portal.dto.response;

public record ExamResponse(

        Long id,
        Long courseId,
        String title,
        Integer durationMinutes,
        Integer totalMarks,
        Integer passingMarks,
        Boolean active

) {}