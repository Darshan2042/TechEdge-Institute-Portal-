package com.techedge.portal.service;

import com.techedge.portal.dto.request.FeedbackRequest;
import com.techedge.portal.dto.response.FeedbackResponse;

import java.util.List;

public interface FeedbackService {

    List<FeedbackResponse> getApprovedFeedback();

    List<FeedbackResponse> getPendingFeedback();

    FeedbackResponse submitFeedback(
            Long userId,
            FeedbackRequest request
    );

    FeedbackResponse approveFeedback(Long feedbackId);

    void deleteFeedback(
            Long feedbackId,
            Long userId,
            boolean admin
    );
}