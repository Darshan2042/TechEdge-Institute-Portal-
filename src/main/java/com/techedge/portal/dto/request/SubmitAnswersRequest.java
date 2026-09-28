package com.techedge.portal.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SubmitAnswersRequest(

        @NotNull(message = "Answers are required")
        @Valid
        List<AnswerRequest> answers

) {

    public record AnswerRequest(

            @NotNull(message = "Question ID is required")
            Long questionId,

            Long selectedOptionId

    ) {}
}