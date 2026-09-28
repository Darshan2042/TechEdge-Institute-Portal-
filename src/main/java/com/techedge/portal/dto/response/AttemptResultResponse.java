package com.techedge.portal.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record AttemptResultResponse(

        Long attemptId,
        Integer score,
        Integer totalMarks,
        Double percentage,
        Boolean passed,
        Integer correctCount,
        Integer wrongCount,
        Integer unansweredCount,
        LocalDateTime submittedAt,
        Boolean expired,
        List<QuestionResultResponse> questions

) {

    public record QuestionResultResponse(

            String questionText,
            Long selectedOptionId,
            Long correctOptionId,
            Boolean isCorrect

    ) {}
}