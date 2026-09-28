package com.techedge.portal.dto.response;

import com.techedge.portal.entity.enums.AttemptStatus;

import java.time.LocalDateTime;

public record AttemptSummaryResponse(

        Long attemptId,

        Long examId,

        String examTitle,

        AttemptStatus status,

        Integer score,

        Integer totalMarks,

        LocalDateTime startedAt,

        LocalDateTime submittedAt
) {
}