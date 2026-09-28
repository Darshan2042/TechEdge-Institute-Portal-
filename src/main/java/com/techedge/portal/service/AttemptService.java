package com.techedge.portal.service;

import com.techedge.portal.dto.request.SubmitAnswersRequest;
import com.techedge.portal.dto.response.AttemptResultResponse;
import com.techedge.portal.dto.response.AttemptStartResponse;
import com.techedge.portal.dto.response.AttemptSummaryResponse;

import java.util.List;

public interface AttemptService {

    AttemptStartResponse startAttempt(
            Long examId,
            Long userId
    );

    AttemptStartResponse getAttempt(
            Long attemptId,
            Long userId
    );

    void saveAnswers(
            Long attemptId,
            Long userId,
            SubmitAnswersRequest request
    );

    AttemptResultResponse submitAttempt(
            Long attemptId,
            Long userId,
            SubmitAnswersRequest request
    );

    AttemptResultResponse getResult(
            Long attemptId,
            Long userId,
            boolean admin
    );

    List<AttemptSummaryResponse> getMyAttempts(
            Long userId
    );

    List<AttemptSummaryResponse> getExamAttempts(
            Long examId
    );
}