package com.techedge.portal.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record AttemptStartResponse(

        Long attemptId,
        String examTitle,
        Integer durationMinutes,
        LocalDateTime startedAt,
        LocalDateTime expiresAt,
        Integer totalMarks,
        List<QuestionResponse> questions

) {

    public record QuestionResponse(

            Long questionId,
            String questionText,
            Integer marks,
            List<OptionResponse> options

    ) {}

    public record OptionResponse(

            Long optionId,
            String optionText

    ) {}
}