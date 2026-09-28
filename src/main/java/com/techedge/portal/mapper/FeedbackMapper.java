package com.techedge.portal.mapper;

import com.techedge.portal.dto.response.FeedbackResponse;
import com.techedge.portal.entity.Feedback;

public class FeedbackMapper {

    private FeedbackMapper() {
    }

    public static FeedbackResponse toResponse(
            Feedback feedback,
            String placedAt) {

        return new FeedbackResponse(
                feedback.getId(),
                feedback.getStudent().getUser().getFullName(),
                feedback.getCourse().getTitle(),
                feedback.getRating(),
                feedback.getComments(),
                feedback.getCreatedAt(),
                placedAt
        );
    }
}