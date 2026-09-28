package com.techedge.portal.mapper;

import com.techedge.portal.dto.response.AttemptStartResponse;
import com.techedge.portal.entity.Option;
import com.techedge.portal.entity.Question;

import java.util.List;

public class AttemptMapper {

    private AttemptMapper() {
    }

    public static AttemptStartResponse.QuestionResponse toQuestionResponse(
            Question question,
            List<Option> options) {

        List<AttemptStartResponse.OptionResponse> optionResponses =
                options.stream()
                        .map(AttemptMapper::toOptionResponse)
                        .toList();

        return new AttemptStartResponse.QuestionResponse(
                question.getId(),
                question.getQuestionText(),
                question.getMarks(),
                optionResponses
        );
    }

    private static AttemptStartResponse.OptionResponse toOptionResponse(
            Option option) {

        return new AttemptStartResponse.OptionResponse(
                option.getId(),
                option.getOptionText()
        );
    }
}