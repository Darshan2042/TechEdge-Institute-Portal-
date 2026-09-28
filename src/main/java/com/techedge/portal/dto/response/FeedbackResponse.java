package com.techedge.portal.dto.response;

import java.time.LocalDateTime;

public record FeedbackResponse(

        Long id,

        String studentName,

        String courseTitle,

        Integer rating,

        String comments,

        LocalDateTime createdAt,

        String placedAt
) {
}